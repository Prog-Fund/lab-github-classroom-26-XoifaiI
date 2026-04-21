package daycare.models;

import daycare.utils.DogBreedUtility;
import java.util.Objects;

/**
 * Dog: a Mammal with a breed and a dangerous flag.
 *
 * <p>weekly fee is the daily rate times number of days attending. dangerous
 * breeds cost more. ctor falls back to {@link #DEFAULT_BREED} on unknown
 * input, setter silently refuses it (spec rule, no else in setters).
 */
public class Dog extends Mammal {

  public static final float NONDANGEROUS_DAILY_RATE = 30.00f;
  public static final float DANGEROUS_DAILY_RATE = 40.00f;
  public static final String DEFAULT_BREED = "Labrador Retriever";

  protected String breed = DEFAULT_BREED;
  protected boolean dangerousBreed;

  public Dog(String name, int age, Owner owner, int id,
      char sex, boolean vaccinated, double weight, boolean neutered,
      String breed, boolean dangerousBreed) {
    super(name, age, owner, id, sex, vaccinated, weight, neutered);
    String canon = DogBreedUtility.canonicalBreed(breed);
    this.breed = canon != null ? canon : DEFAULT_BREED;
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
    String canon = DogBreedUtility.canonicalBreed(breed);
    if (canon != null) {
      this.breed = canon;
    }
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
    return Objects.hash(breed, dangerousBreed);
  }

  @Override
  public String toString() {
    return super.toString()
        + String.format(" breed=%s dangerous=%b fee=%.2f",
            breed, dangerousBreed, calculateWeeklyFee());
  }
}
