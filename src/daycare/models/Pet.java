package daycare.models;

import daycare.utils.Utilities;
import java.util.Arrays;

/**
 * Pet: abstract base for any animal staying at the daycare.
 *
 * <p>holds id, name, age, owner, and which days of the week its attending.
 * subclasses (Dog / Cat / Parrot via Mammal / Bird) add their own species
 * fields and pin down the weekly fee.
 *
 * <p>id is auto assigned from {@link #nextId} starting at {@link #FIRST_ID}.
 * callers can still pass an id explicitly. if its >= {@link #FIRST_ID} we
 * trust it and bump the counter past it, anything smaller means "give me a
 * fresh one".
 */
public abstract class Pet {

  /** mon = 0, sat = 5. no sunday slot, the daycare is closed. */
  public static final int DAYS_PER_WEEK = 6;

  /** spec says ids start at 1000. */
  public static final int FIRST_ID = 1000;

  public static final int MAX_NAME_LENGTH = 30;

  private static int nextId = FIRST_ID;

  protected int id;
  protected String name;
  protected int age;
  protected Owner owner;
  protected boolean[] daysAttending;

  protected Pet(String name, int age, Owner owner, int id) {
    // assign fields directly instead of going through the public setters.
    // calling overridable methods from a ctor runs the override before its
    // own fields are initialized.
    this.name = Utilities.truncate(name, MAX_NAME_LENGTH);
    this.age = age;
    this.owner = owner;
    this.id = assignId(id);
    this.daysAttending = new boolean[DAYS_PER_WEEK];
    Arrays.fill(this.daysAttending, false);
  }

  public abstract double calculateWeeklyFee();

  public void checkIn(int day) {
    if (Utilities.validRange(day, 0, DAYS_PER_WEEK - 1)) {
      daysAttending[day] = true;
    }
  }

  public void checkOut(int day) {
    if (Utilities.validRange(day, 0, DAYS_PER_WEEK - 1)) {
      daysAttending[day] = false;
    }
  }

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
    if (name != null) {
      this.name = Utilities.truncate(name, MAX_NAME_LENGTH);
    }
  }

  /** same effect as {@link #setName(String)}, kept around bc the UML lists both. */
  public void initName(String name) {
    if (name != null) {
      this.name = Utilities.truncate(name, MAX_NAME_LENGTH);
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
    // defensive copy: a caller could otherwise flip days past the checkIn /
    // checkOut guard and skip the validRange check.
    return Arrays.copyOf(daysAttending, daysAttending.length);
  }

  public void setDaysAttending(boolean[] daysAttending) {
    if (daysAttending != null && daysAttending.length == DAYS_PER_WEEK) {
      this.daysAttending = Arrays.copyOf(daysAttending, DAYS_PER_WEEK);
    }
  }

  @Override
  public String toString() {
    return String.format(
        "Pet{id=%d, name=%s, age=%d, owner=%s, daysAttending=%d/%d}",
        id, name, age, owner != null ? owner.getName() : "none",
        numOfDaysAttending(), DAYS_PER_WEEK);
  }

  private static int assignId(int requested) {
    if (requested >= FIRST_ID) {
      if (requested >= nextId) {
        nextId = requested + 1;
      }
      return requested;
    }
    return nextId++;
  }

  /**
   * advances {@link #nextId} past every id in {@code loaded}. xstream builds
   * pets via reflection and skips the ctor, so the static counter stays at
   * whatever it was before the load. callers should run this after
   * deserialising so fresh pets dont collide with restored ones.
   */
  public static void recomputeNextId(Iterable<? extends Pet> loaded) {
    if (loaded == null) {
      return;
    }
    for (Pet p : loaded) {
      if (p != null && p.id >= nextId) {
        nextId = p.id + 1;
      }
    }
  }
}
