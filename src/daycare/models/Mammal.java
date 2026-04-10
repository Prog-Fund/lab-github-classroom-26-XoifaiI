package daycare.models;

/**
 * Mammal: abstract Pet that has fur, a sex, a weight, and the usual vet stuff.
 *
 * <p>middle layer between Pet and the concrete Dog / Cat classes. doesnt know
 * how to compute its own weekly fee yet, thats still abstract, the dog and cat
 * subclasses pin it down.
 *
 * <p>validation rules per spec: weight must be between {@link #MIN_WEIGHT} and
 * {@link #MAX_WEIGHT} kg, otherwise the default ({@link #DEFAULT_WEIGHT}) sticks.
 * sex must be one of M / F / U, anything else falls back to {@link #DEFAULT_SEX}.
 *
 * <p>Returns: abstract class. instantiate Dog or Cat instead.
 *
 * <p>Example, once a concrete subclass exists:
 * <pre>{@code
 * Owner alice = new Owner(1, "Alice", "12 Main St", "555-0100", "a@x.com");
 * Mammal pet = ...; // any Dog or Cat
 * pet.setNeutered(true);
 * pet.setWeight(25.5);
 * char sex = pet.getSex();
 * }</pre>
 */
public abstract class Mammal extends Pet {

  /** lowest acceptable weight in kg. anything under this is a typo or a hamster. */
  public static final double MIN_WEIGHT = 2;

  /** highest acceptable weight in kg. anything over this is not a daycare problem. */
  public static final double MAX_WEIGHT = 200;

  /** what weight a mammal gets if the ctor was handed something out of range. */
  public static final double DEFAULT_WEIGHT = 2;

  /** sex code used when the caller didnt give us anything sensible. */
  public static final char DEFAULT_SEX = 'M';

  protected char sex = DEFAULT_SEX;
  protected boolean neutered;
  protected double weight = DEFAULT_WEIGHT;
  protected boolean vaccinated;

  protected Mammal(String name, int age, Owner owner, int id,
      char sex, boolean vaccinated, double weight, boolean neutered) {
    super(name, age, owner, id);
    // ctors apply defaults on bad input, setters dont. keep the two paths
    // separate so the spec rule "no else in setters" stays clean.
    this.sex = isValidSex(sex) ? sex : DEFAULT_SEX;
    this.weight = isValidWeight(weight) ? weight : DEFAULT_WEIGHT;
    this.vaccinated = vaccinated;
    this.neutered = neutered;
  }

  public char getSex() {
    return sex;
  }

  public void setSex(char sex) {
    if (isValidSex(sex)) {
      this.sex = sex;
    }
  }

  public boolean isNeutered() {
    return neutered;
  }

  public void setNeutered(boolean neutered) {
    this.neutered = neutered;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    if (isValidWeight(weight)) {
      this.weight = weight;
    }
  }

  public boolean isVaccinated() {
    return vaccinated;
  }

  public void setVaccinated(boolean vaccinated) {
    this.vaccinated = vaccinated;
  }

  @Override
  public String toString() {
    return super.toString()
        + String.format(
            " sex=%c vaccinated=%b weight=%.2f neutered=%b",
            sex, vaccinated, weight, neutered);
  }

  private static boolean isValidWeight(double w) {
    return w >= MIN_WEIGHT && w <= MAX_WEIGHT;
  }

  private static boolean isValidSex(char s) {
    char up = Character.toUpperCase(s);
    return up == 'M' || up == 'F' || up == 'U';
  }
}
