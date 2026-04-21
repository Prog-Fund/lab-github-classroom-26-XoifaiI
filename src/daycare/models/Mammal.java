package daycare.models;

/**
 * Mammal: abstract Pet with fur, sex, weight, vaccination, neutered flag.
 *
 * <p>ctors apply defaults on bad input, setters only update when valid. spec
 * rule, keeps the two paths separate so the "no else in setters" invariant
 * stays clean.
 */
public abstract class Mammal extends Pet {

  public static final double MIN_WEIGHT = 2;
  public static final double MAX_WEIGHT = 200;
  public static final double DEFAULT_WEIGHT = 2;
  public static final char DEFAULT_SEX = 'M';

  protected char sex = DEFAULT_SEX;
  protected boolean neutered;
  protected double weight = DEFAULT_WEIGHT;
  protected boolean vaccinated;

  protected Mammal(String name, int age, Owner owner, int id,
      char sex, boolean vaccinated, double weight, boolean neutered) {
    super(name, age, owner, id);
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
