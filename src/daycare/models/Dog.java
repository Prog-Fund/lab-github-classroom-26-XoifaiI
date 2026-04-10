package daycare.models;

import java.util.Objects;

/**
 * Dog: a Mammal that comes with a breed and a "is this breed dangerous" flag.
 *
 * <p>weekly fee is the relevant daily rate x number of days the dog is
 * attending. dangerous breeds cost more bc they need more careful handling.
 * the rates are public constants on this class so the menu / reports layer
 * can show them without having to instantiate a dog.
 *
 * <p>Returns: instance of Dog.
 *
 * <p>Example:
 * <pre>{@code
 * Owner alice = new Owner(1, "Alice", "12 Main St", "555-0100", "a@x.com");
 * Dog rex = new Dog("Rex", 3, alice, 100,
 *     'M', true, 25.5, true, "Labrador", false);
 * rex.checkIn(0);
 * rex.checkIn(1);
 * rex.checkIn(2);
 * double fee = rex.calculateWeeklyFee(); // 15.00 * 3 = 45.00
 * }</pre>
 */
public class Dog extends Mammal {

  /** what a normal-breed dog costs per attending day. */
  public static final float NONDANGEROUS_DAILY_RATE = 15.00f;

  /** what a dangerous breed dog costs per attending day. */
  public static final float DANGEROUS_DAILY_RATE = 25.00f;

  protected String breed;
  protected boolean dangerousBreed;

  public Dog(String name, int age, Owner owner, int id,
      char sex, boolean neutered, double weight, boolean vaccinated,
      String breed, boolean dangerousBreed) {
    super(name, age, owner, id, sex, neutered, weight, vaccinated);
    this.breed = breed;
    this.dangerousBreed = dangerousBreed;
  }

  @Override
  public double calculateWeeklyFee() {
    float rate = dangerousBreed ? DANGEROUS_DAILY_RATE : NONDANGEROUS_DAILY_RATE;
    return rate * numOfDaysAttending();
  }

  public String getBreed() {
    return breed;
  }

  public void setBreed(String breed) {
    this.breed = breed;
  }

  public boolean isDangerousBreed() {
    return dangerousBreed;
  }

  public void setDangerousBreed(boolean dangerousBreed) {
    this.dangerousBreed = dangerousBreed;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Dog that)) {
      return false;
    }
    return dangerousBreed == that.dangerousBreed && Objects.equals(breed, that.breed);
  }

  @Override
  public int hashCode() {
    // UML doesnt list hashCode on Dog but java's contract says if u override
    // equals u have to override hashCode too, otherwise HashMap/HashSet break
    return Objects.hash(breed, dangerousBreed);
  }

  @Override
  public String toString() {
    return super.toString()
        + String.format(" breed=%s dangerous=%b fee=%.2f",
            breed, dangerousBreed, calculateWeeklyFee());
  }
}
