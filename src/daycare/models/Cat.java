package daycare.models;

/**
 * Cat: a Mammal that has a favourite toy and an indoor/outdoor preference.
 *
 * <p>weekly fee depends on whether its an indoor cat (cheaper) or outdoor
 * (slightly more bc theyre messier and need more cleaning). favouriteToy is
 * a free-form String, the menu layer can suggest values from CatToyUtility
 * but theres no enforcement at the model level.
 *
 * <p>Returns: instance of Cat.
 *
 * <p>Example:
 * <pre>{@code
 * Owner alice = new Owner(1, "Alice", "12 Main St", "555-0100", "a@x.com");
 * Cat whiskers = new Cat("Whiskers", 5, alice, 200,
 *     'F', true, 4.2, true, true, "Feather Wand");
 * whiskers.checkIn(0);
 * whiskers.checkIn(2);
 * double fee = whiskers.calculateWeeklyFee(); // 10.00 * 2 = 20.00
 * }</pre>
 */
public class Cat extends Mammal {

  /** what an indoor cat costs per attending day. */
  public static final float INDOOR_DAILY_RATE = 10.00f;

  /** what an outdoor cat costs per attending day. they need more cleaning, hence pricier. */
  public static final float OUTDOOR_DAILY_RATE = 12.50f;

  protected boolean indoorCat;
  protected String favouriteToy;

  public Cat(String name, int age, Owner owner, int id,
      char sex, boolean neutered, double weight, boolean vaccinated,
      boolean indoorCat, String favouriteToy) {
    super(name, age, owner, id, sex, neutered, weight, vaccinated);
    this.indoorCat = indoorCat;
    this.favouriteToy = favouriteToy;
  }

  @Override
  public double calculateWeeklyFee() {
    float rate = indoorCat ? INDOOR_DAILY_RATE : OUTDOOR_DAILY_RATE;
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
    this.favouriteToy = favouriteToy;
  }

  @Override
  public String toString() {
    return super.toString()
        + String.format(" indoor=%b toy=%s fee=%.2f",
            indoorCat, favouriteToy, calculateWeeklyFee());
  }
}
