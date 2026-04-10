package daycare.models;

import java.util.Arrays;

/**
 * Pet: abstract base for any animal staying at the daycare.
 *
 * <p>holds the stuff every pet has regardless of species: id, name, age,
 * who owns it, and which days of the week its attending. subclasses
 * (Dog, Cat, Parrot via Mammal/Bird) add their own species fields and pin
 * down how the weekly fee actually gets computed.
 *
 * <p>id gets assigned by PetsDayCareAPI on add, the user never picks it.
 * daysAttending is a 7-slot Boolean array (mon = 0, sun = 6), seeded to all
 * FALSE in the constructor so {@link #numOfDaysAttending()} is always safe
 * to call.
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

  /** how many slots daysAttending holds. spoiler: 7. */
  public static final int DAYS_PER_WEEK = 7;

  protected int id;
  protected String name;
  protected int age;
  protected Owner owner;
  protected Boolean[] daysAttending;

  protected Pet(String name, int age, Owner owner, int id) {
    // assign fields directly instead of going through the public setters,
    // calling overridable methods from a ctor is the classic java footgun
    // (subclass override runs before its own fields are initialized)
    this.name = name;
    this.age = age;
    this.owner = owner;
    this.id = id;
    this.daysAttending = new Boolean[DAYS_PER_WEEK];
    // seed to all FALSE so checkIn/Out and numOfDaysAttending dont have to
    // worry about nulls in the common path. setDaysAttending could still
    // smuggle nulls in later (eg from xstream load), numOfDaysAttending
    // handles that defensively.
    Arrays.fill(this.daysAttending, Boolean.FALSE);
  }

  /** Subclasses say how much they cost the owner per week. */
  public abstract double calculateWeeklyFee();

  /** Marks this pet as attending on {@code day} (0 = mon, 6 = sun). */
  public void checkIn(int day) {
    daysAttending[day] = Boolean.TRUE;
  }

  /** Clears attendance for {@code day}. */
  public void checkOut(int day) {
    daysAttending[day] = Boolean.FALSE;
  }

  /** Counts how many days this pet is actually showing up this week. */
  public int numOfDaysAttending() {
    int total = 0;
    for (Boolean day : daysAttending) {
      // null safe in case xstream rehydrates a sparse array
      if (day != null && day) {
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
    this.name = name;
  }

  /**
   * Same effect as {@link #setName(String)}, kept around bc the UML lists both.
   *
   * <p>convention in this codebase: use initName from constructors / first-time
   * setup, setName for later mutations. functionally identical right now.
   */
  public void initName(String name) {
    this.name = name;
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
    this.owner = owner;
  }

  public Boolean[] getDaysAttending() {
    return daysAttending;
  }

  public void setDaysAttending(Boolean[] daysAttending) {
    this.daysAttending = daysAttending;
  }

  @Override
  public String toString() {
    return String.format(
        "Pet{id=%d, name=%s, age=%d, owner=%s, daysAttending=%d/7}",
        id, name, age, owner != null ? owner.getName() : "none", numOfDaysAttending());
  }
}
