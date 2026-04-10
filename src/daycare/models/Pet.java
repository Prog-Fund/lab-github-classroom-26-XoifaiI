package daycare.models;

import daycare.utils.Utilities;
import java.util.Arrays;

/**
 * Pet: abstract base for any animal staying at the daycare.
 *
 * <p>holds the stuff every pet has regardless of species: id, name, age,
 * who owns it, and which days of the week its attending. subclasses
 * (Dog, Cat, Parrot via Mammal/Bird) add their own species fields and pin
 * down how the weekly fee actually gets computed.
 *
 * <p>id is auto assigned from a static {@link #nextId} counter that starts at
 * {@link #FIRST_ID} (1000), so every Pet ever constructed gets a unique id
 * the user never has to pick. callers can still pass an id explicitly, if
 * its >= {@link #FIRST_ID} we trust it and bump the counter past it; anything
 * smaller (incl. the usual 0 sentinel) means "give me a fresh one".
 *
 * <p>daysAttending is a 6 slot primitive boolean array (mon = 0, sat = 5).
 * urban tails is closed sundays so theres no slot for it. seeded to all
 * false in the ctor so {@link #numOfDaysAttending()} is always safe to call.
 *
 * <p>Returns: abstract class. instantiate one of the concrete subclasses
 * (Dog / Cat / Parrot) instead.
 *
 * <p>Example, once a concrete subclass exists:
 * <pre>{@code
 * Owner alice = new Owner(1, "Alice", "12 Main St", "555-0100", "a@x.com");
 * Pet rex = ...; // any Dog / Cat / Parrot
 * rex.setOwner(alice);
 * rex.checkIn(0);
 * rex.checkIn(1);
 * int days = rex.numOfDaysAttending();
 * double fee = rex.calculateWeeklyFee();
 * }</pre>
 */
public abstract class Pet {

  /** how many slots daysAttending holds. mon..sat, no sundays bc the daycare is closed. */
  public static final int DAYS_PER_WEEK = 6;

  /** first id ever handed out. spec says ids are >= 1000. */
  public static final int FIRST_ID = 1000;

  /** max length for the name field, anything longer gets truncated in the ctor/setter. */
  public static final int MAX_NAME_LENGTH = 30;

  /** rolling id counter shared across every Pet instance. */
  private static int nextId = FIRST_ID;

  protected int id;
  protected String name;
  protected int age;
  protected Owner owner;
  protected boolean[] daysAttending;

  protected Pet(String name, int age, Owner owner, int id) {
    // assign fields directly instead of going through the public setters,
    // calling overridable methods from a ctor is the classic java footgun
    // (subclass override runs before its own fields are initialized)
    this.name = truncate(name, MAX_NAME_LENGTH);
    this.age = age;
    // spec says owner "must be a valid owner", we treat null as invalid and
    // leave the field null. callers can fix it later via setOwner.
    this.owner = owner;
    this.id = assignId(id);
    this.daysAttending = new boolean[DAYS_PER_WEEK];
    // primitive booleans default to false, no need to fill explicitly, but
    // doing it anyway keeps the intent obvious if anyone changes the type.
    Arrays.fill(this.daysAttending, false);
  }

  /** Subclasses say how much they cost the owner per week. */
  public abstract double calculateWeeklyFee();

  /** Marks this pet as attending on {@code day} (0 = mon, 5 = sat). */
  public void checkIn(int day) {
    if (Utilities.validRange(day, 0, DAYS_PER_WEEK - 1)) {
      daysAttending[day] = true;
    }
  }

  /** Clears attendance for {@code day} (0 = mon, 5 = sat). */
  public void checkOut(int day) {
    if (Utilities.validRange(day, 0, DAYS_PER_WEEK - 1)) {
      daysAttending[day] = false;
    }
  }

  /** Counts how many days this pet is actually showing up this week. */
  public int numOfDaysAttending() {
    int total = 0;
    for (boolean day : daysAttending) {
      if (day) {
        total++;
      }
    }
    return total;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    // setters dont apply defaults, they only update if the value passed in is
    // valid. for name "valid" means non-null, and we still truncate.
    if (name != null) {
      this.name = truncate(name, MAX_NAME_LENGTH);
    }
  }

  /**
   * Same effect as {@link #setName(String)}, kept around bc the UML lists both.
   *
   * <p>convention in this codebase: use initName from constructors / first-time
   * setup, setName for later mutations. functionally identical right now.
   */
  public void initName(String name) {
    if (name != null) {
      this.name = truncate(name, MAX_NAME_LENGTH);
    }
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public Owner getOwner() {
    return owner;
  }

  public void setOwner(Owner owner) {
    if (owner != null) {
      this.owner = owner;
    }
  }

  public boolean[] getDaysAttending() {
    return daysAttending;
  }

  public void setDaysAttending(boolean[] daysAttending) {
    if (daysAttending != null && daysAttending.length == DAYS_PER_WEEK) {
      this.daysAttending = daysAttending;
    }
  }

  @Override
  public String toString() {
    return String.format(
        "Pet{id=%d, name=%s, age=%d, owner=%s, daysAttending=%d/%d}",
        id, name, age, owner != null ? owner.getName() : "none",
        numOfDaysAttending(), DAYS_PER_WEEK);
  }

  /**
   * Picks an id for a new Pet.
   *
   * <p>if the caller passed an id >= {@link #FIRST_ID} we keep it and bump
   * {@link #nextId} past it (so loaded pets dont collide with new ones). if
   * they passed anything smaller, incl the 0 sentinel the menu uses, we hand
   * out the next id from the counter.
   */
  private static int assignId(int requested) {
    if (requested >= FIRST_ID) {
      if (requested >= nextId) {
        nextId = requested + 1;
      }
      return requested;
    }
    return nextId++;
  }

  /** chops {@code value} down to {@code max} chars. null in -> null out, callers handle that. */
  private static String truncate(String value, int max) {
    if (value == null) {
      return null;
    }
    return value.length() <= max ? value : value.substring(0, max);
  }
}
