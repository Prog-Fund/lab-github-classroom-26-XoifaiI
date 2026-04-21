package daycare.models;

import java.util.Objects;

/**
 * Bird: abstract Pet with feathers, a wingspan, and a can fly flag.
 *
 * <p>UML puts equals / hashCode on Bird specifically (Mammal doesnt have
 * them), so two birds count as equal when their wingSpan and canFly match.
 */
public abstract class Bird extends Pet {

  public static final double MIN_WINGSPAN = 3;
  public static final double MAX_WINGSPAN = 400;
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
