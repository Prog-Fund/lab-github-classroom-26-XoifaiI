package daycare.models;

/**
 * Cat: a Mammal that has a favourite toy and an indoor/outdoor preference.
 *
 * <p>weekly fee starts at {@link #BASE_DAILY_RATE} per day, indoor cats add
 * {@link #INDOOR_SURCHARGE} on top bc they need extra attention/cleaning when
 * theyre cooped up. favouriteToy is a free-form String, the menu layer can
 * suggest values from {@code CatToyUtility} but theres no enforcement at the
 * model level.
 *
 * <p>Returns: instance of Cat.
 *
 * <p>Example:
 * <pre>{@code
 * Owner alice = new Owner(1, "Alice", "12 Main St", "555-0100", "a@x.com");
 * Cat whiskers = new Cat("Whiskers", 5, alice, 0,
 *     'F', true, 4.2, true, true, "Feather Wand");
 * whiskers.checkIn(0);
 * whiskers.checkIn(2);
 * double fee = whiskers.calculateWeeklyFee(); // (20 + 5) * 2 = 50.00
 * }</pre>
 */
public class Cat extends Mammal {

  /** base daily rate per attending day, every cat pays this. */
  public static final float BASE_DAILY_RATE = 20.00f;

  /** extra per attending day if the cat is an indoor only cat. */
  public static final float INDOOR_SURCHARGE = 5.00f;

  /** indoorCat default per spec, urban tails assumes indoor unless told otherwise. */
  public static final boolean DEFAULT_INDOOR = true;

  /** favouriteToy default per spec, used when nothing was passed in. */
  public static final String DEFAULT_TOY = "not known";

  protected boolean indoorCat = DEFAULT_INDOOR;
  protected String favouriteToy = DEFAULT_TOY;

  public Cat(String name, int age, Owner owner, int id,
      char sex, boolean vaccinated, double weight, boolean neutered,
      boolean indoorCat, String favouriteToy) {
    super(name, age, owner, id, sex, vaccinated, weight, neutered);
    this.indoorCat = indoorCat;
    // string "validation" here is just null/blank -> default. anything else
    // goes through. CatToyUtility could be used for stricter checks but spec
    // doesnt require it.
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
