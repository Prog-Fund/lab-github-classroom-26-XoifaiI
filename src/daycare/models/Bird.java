package daycare.models;

import java.util.Objects;

/**
 * Bird: abstract Pet with feathers, a wingspan, and a "can it actually fly" flag.
 *
 * <p>middle layer between Pet and the concrete Parrot class. matches the UML
 * which puts equals/hashCode on Bird specifically (Mammal doesnt have them),
 * so two birds count as equal when their wingSpan and canFly match.
 *
 * <p>validation rules per spec: wingspan must be between {@link #MIN_WINGSPAN}
 * and {@link #MAX_WINGSPAN} cm, otherwise the default ({@link #DEFAULT_WINGSPAN})
 * sticks. canFly defaults to false.
 *
 * <p>Returns: abstract class. instantiate Parrot instead.
 *
 * <p>Example, once a concrete subclass exists:
 * <pre>{@code
 * Owner bob = new Owner(2, "Bob", "1 Oak Rd", "555-0200", "b@x.com");
 * Bird bird = ...; // any Parrot
 * bird.setWingSpan(40);
 * bird.setCanFly(true);
 * boolean flier = bird.isCanFly();
 * }</pre>
 */
public abstract class Bird extends Pet {

  /** smallest acceptable wingspan in cm. tinier than this and its a bug. */
  public static final double MIN_WINGSPAN = 3;

  /** largest acceptable wingspan in cm. anything bigger isnt fitting in the daycare. */
  public static final double MAX_WINGSPAN = 400;

  /** wingspan used when the ctor was handed something out of range. */
  public static final double DEFAULT_WINGSPAN = 3;

  protected double wingSpan = DEFAULT_WINGSPAN;
  protected boolean canFly;

  protected Bird(String name, int age, Owner owner, int id,
      double wingSpan, boolean canFly) {
    super(name, age, owner, id);
    this.wingSpan = isValidWingSpan(wingSpan) ? wingSpan : DEFAULT_WINGSPAN;
    this.canFly = canFly;
  }

  public double getWingSpan() {
    return wingSpan;
  }

  public void setWingSpan(double wingSpan) {
    if (isValidWingSpan(wingSpan)) {
      this.wingSpan = wingSpan;
    }
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

  private static boolean isValidWingSpan(double w) {
    return w >= MIN_WINGSPAN && w <= MAX_WINGSPAN;
  }
}
