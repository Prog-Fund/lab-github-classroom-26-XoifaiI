package daycare.models;

/**
 * Mammal: abstract Pet that has fur, a sex, a weight, and the usual vet stuff.
 *
 * <p>middle layer between Pet and the concrete Dog / Cat classes. doesnt know
 * how to compute its own weekly fee yet, thats still abstract, the dog and cat
 * subclasses pin it down.
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

  protected char sex;
  protected boolean neutered;
  protected double weight;
  protected boolean vaccinated;

  protected Mammal(String name, int age, Owner owner, int id,
      char sex, boolean neutered, double weight, boolean vaccinated) {
    super(name, age, owner, id);
    this.sex = sex;
    this.neutered = neutered;
    this.weight = weight;
    this.vaccinated = vaccinated;
  }

  public char getSex() {
    return sex;
  }

  public void setSex(char sex) {
    this.sex = sex;
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
    this.weight = weight;
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
            " sex=%c neutered=%b weight=%.2f vaccinated=%b",
            sex, neutered, weight, vaccinated);
  }
}
