# Reflection

Name: Jack Gannon

Student Number: 20117425

## Chronology

Started by reading the spec end to end and mentally noting the things that
were going to bite later. Main flags were race conditions on the static Pet id
counter once persistence came in, magic values littered everywhere in the spec
(rates, day counts, name lengths, vocab boundaries) which all needed pulling
into named constants, and the equals / hashCode contract on Bird and Dog being
spec mandated overrides that needed instanceof + null checks doing properly.

Actually started coding with the TUI because i wanted something nicer to look at
than plain System.out.println spam while iterating. wrote Tui first (colour /
sanitise / clear / width), then Panel and Banner and Table, then the Menu
builder on top. having the rendering layer in place early meant every later
feature got a usable demo screen for free.

Then the inheritance hierarchy. Pet first, then Mammal and Bird, then the
three concretes (Dog, Cat, Parrot). validation rules went in as i built each
class so the constructor / setter split (defaults in ctor, no else in setter)
was consistent everywhere. constants got named at field declaration time
instead of being inlined.

next was PetsDayCareAPI. basic CRUD first, then the reporting / counting
methods (lots of small variations on "filter pets by predicate, count or list
them"), then the sort routines. sorts are hand rolled binary insertion sort bc
the spec bans library sort.

Driver came after that, wiring the existing TUI to the controllers. it is the
only class that touches stdin or stdout.

persistence (XStream) and the OwnerAPI / Owner classes were the last big
chunks before tests. Tests were written last after manually going over the code noting
what should be tested.

## Main difficulties

**spec was confusing.** The document contradicts itself in a few places. the
daysAttending sample shows 6 elements, the default shows 7, and the checkIn
validation says day index 0..5 (6 days). i went with 6 (Mon..Sat) because thats what
the validation rule and the "closed sundays" framing both imply. There are
also typos in the API method descriptions (numberOfParrots described as
"returns the number of Dog objects", listAllParrots saying "details of all the
dogs", getPetById saying "ignoring case" on an int). coverage targets disagree
across two tables (85% vs 90%). the UML diagrams disagree with each other on
the Pet method name (numOfDaysInKennel vs numOfDaysAttending) and on the API
delete method name (removePet vs deletePetByIndex / deletePetById). I went
with what the spec text said and added aliases.

**TUI on a dualboot setup.** I dualboot windows and linux and the TUI behaved
completely differently between the two. windows terminal handles ANSI escapes
fine in modern releases but only after a virtual terminal hint gets sent, the
old conhost ate the colour codes raw. linux just worked. I ended up putting a
small enableColor probe in Tui that opts in on windows when the env reports
ANSICON / WT_SESSION / a TERM other than dumb, and sanitising any string going
out to stdout so a stray escape sequence in a loaded xml file cant repaint the
screen. clearScreen / pausePrompt also had to be platform aware bc the cls vs
clear distinction kept biting me.

**XStream being a pain.** Two issues. first, modern XStream (1.4.18+) requires
an allowTypes allowlist or it refuses to deserialise anything for security. So
both APIs declare ALLOWED_TYPES and pass it on load. Second, naive save
(open file, write, close) corrupts the data file if the JVM crashes mid write,
which happened to me twice while debugging. The fix was atomic save: write to a
sibling .tmp first, then atomic move it into place via Files.move with
ATOMIC_MOVE. windows + some network filesystems dont support ATOMIC_MOVE so
theres a fallback to plain REPLACE_EXISTING. either way the old data file stays
intact until the new one is fully written.

a smaller pain was that XStream uses reflection and skips the Pet ctor on
load, so the static nextId counter never advanced past loaded ids and fresh
pets collided with restored ones. recomputeNextId(loaded) on the Pet class
fixes that at the end of every successful load.

## Bugs / unfinished

No known bugs. Coverage is past the spec target on every class i wrote tests
for.Ii deviated from the suggested menu layout (more reports, sectioned sub
menus, a banner) which the spec explicitly allows.

A few spec leftovers i did not implement because they looked redundant or not
useful: there is no separate Pet update menu (update is covered by remove +
re add).

## Main learnings

Biggest one is the difference between "looks fine in dev" and "survives a
crash". the XStream save bug only showed up because the JVM was killed mid write,
and it would have been awful to debug if it hit a user. atomic file writes are
cheap and i should have reached for them earlier.

Also picked up a better feel for the inheritance hierarchy split. Abstract
classes with template methods (calculateWeeklyFee being abstract, ctor and
setters enforcing invariants) means the controller can treat every Pet
uniformly without instanceof noise except where the report explicitly is
"only dogs" or similar.

Testing wise, the chaos_ tests caught a few issues: NaN weight, infinity wingspan, unicode owner names, double
checkIn idempotency, the empty list sort being a no op. Several of those were
bugs the first time i ran them.

##References
- Escape Codes: https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797
- JUnit Examples: https://github.com/junit-team/junit-examples
- Some test ideas came from: https://github.com/CodeIntelligenceTesting/jazzer/