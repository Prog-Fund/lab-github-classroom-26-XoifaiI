package daycare.models;

/**
 * Cat: a Mammal with a favourite toy and an indoor / outdoor flag.
 *
 * <p>weekly fee is {@link #BASE_DAILY_RATE} per day plus {@link #INDOOR_SURCHARGE}
 * for indoor cats. favouriteToy is free form, the menu can suggest values
 * from CatToyUtility but the model doesnt enforce them.
 */
public class Cat extends Mammal {

  public static final float BASE_DAILY_RATE = 20.00f;
  public static final float INDOOR_SURCHARGE = 5.00f;
  public static final boolean DEFAULT_INDOOR = true;
  public static final String DEFAULT_TOY = "not known";

  protected boolean indoorCat = DEFAULT_INDOOR;
  protected String favouriteToy = DEFAULT_TOY;

  public Cat(String name, int age, Owner owner, int id,
      char sex, boolean vaccinated, double weight, boolean neutered,
      boolean indoorCat, String favouriteToy) {
    super(name, age, owner, id, sex, vaccinated, weight, neutered);
    this.indoorCat = indoorCat;
    this.favouriteToy =
        (favouriteToy != null && !favouriteToy.isBlank()) ? favouriteToy : DEFAULT_TOY;
  }

  @Override
  public double calculateWeeklyFee() {
    float rate = indoorCat ? BASE_DAILY_RATE + INDOOR_SURCHARGE : BASE_DAILY_RATE;
    return rate * numOfDaysAttending();
  }

  public boolean isIndoorCat() {
    return indoorCat;
  }

  public void setIndoorCat(boolean indoorCat) {
    this.indoorCat = indoorCat;
  }

  public String getFavouriteToy() {
    return favouriteToy;
  }

  public void setFavouriteToy(String favouriteToy) {
    if (favouriteToy != null && !favouriteToy.isBlank()) {
      this.favouriteToy = favouriteToy;
    }
  }

  @Override
  public String toString() {
    return super.toString()
        + String.format(" indoor=%b toy=%s fee=%.2f",
            indoorCat, favouriteToy, calculateWeeklyFee());
  }
}
