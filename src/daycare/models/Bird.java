package daycare.models;

import java.util.Objects;

/**
 * Bird: abstract Pet with feathers, a wingspan, and a "can it actually fly" flag.
 *
 * <p>middle layer between Pet and the concrete Parrot class. matches the UML
 * which puts equals/hashCode on Bird specifically (Mammal doesnt have them),
 * so two birds count as equal when their wingSpan and canFly match.
 *
 * <p>Returns: abstract class. instantiate Parrot instead.
 *
 * <p>Example, once a concrete subclass exists:
 * <pre>{@code
 * Owner bob = new Owner(2, "Bob", "1 Oak Rd", "555-0200", "b@x.com");
 * Bird bird = ...; // any Parrot
 * bird.setWingSpan(0.4);
 * bird.setCanFly(true);
 * boolean flier = bird.isCanFly();
 * }</pre>
 */
public abstract class Bird extends Pet {

  protected double wingSpan;
  protected boolean canFly;

  protected Bird(String name, int age, Owner owner, int id,
      double wingSpan, boolean canFly) {
    super(name, age, owner, id);
    this.wingSpan = wingSpan;
    this.canFly = canFly;
  }

  public double getWingSpan() {
    return wingSpan;
  }

  public void setWingSpan(double wingSpan) {
    this.wingSpan = wingSpan;
  }

  public boolean isCanFly() {
    return canFly;
  }

  public void setCanFly(boolean canFly) {
    this.canFly = canFly;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Bird that)) {
      return false;
    }
    return Double.compare(wingSpan, that.wingSpan) == 0 && canFly == that.canFly;
  }

  @Override
  public int hashCode() {
    return Objects.hash(wingSpan, canFly);
  }

  @Override
  public String toString() {
    return super.toString()
        + String.format(" wingSpan=%.2f canFly=%b", wingSpan, canFly);
  }
}
