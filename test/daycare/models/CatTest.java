package daycare.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CatTest {

  private Cat cat(boolean indoor, String toy) {
    return new Cat("Whiskers", 4, null, 0, 'F', true, 4.5, true, indoor, toy);
  }

  @Test void constructorStoresFields() {
    Cat c = cat(true, "Feather Wand");
    assertTrue(c.isIndoorCat());
    assertEquals("Feather Wand", c.getFavouriteToy());
  }

  @Test void nullToyFallsBackToDefault() {
    Cat c = cat(true, null);
    assertEquals(Cat.DEFAULT_TOY, c.getFavouriteToy());
  }

  @Test void blankToyFallsBackToDefault() {
    Cat c = cat(true, "   ");
    assertEquals(Cat.DEFAULT_TOY, c.getFavouriteToy());
  }

  @Test void emptyToyFallsBackToDefault() {
    Cat c = cat(true, "");
    assertEquals(Cat.DEFAULT_TOY, c.getFavouriteToy());
  }

  @Test void setIndoorCatToggles() {
    Cat c = cat(true, "Tunnel");
    c.setIndoorCat(false);
    assertFalse(c.isIndoorCat());
  }

  @Test void setFavouriteToyUpdates() {
    Cat c = cat(true, "Tunnel");
    c.setFavouriteToy("Laser Pointer");
    assertEquals("Laser Pointer", c.getFavouriteToy());
  }

  @Test void setFavouriteToyIgnoresNull() {
    Cat c = cat(true, "Tunnel");
    c.setFavouriteToy(null);
    assertEquals("Tunnel", c.getFavouriteToy());
  }

  @Test void setFavouriteToyIgnoresBlank() {
    Cat c = cat(true, "Tunnel");
    c.setFavouriteToy("   ");
    assertEquals("Tunnel", c.getFavouriteToy());
  }

  @Test void weeklyFeeZeroDaysIsZero() {
    Cat c = cat(true, "Tunnel");
    assertEquals(0, c.calculateWeeklyFee());
  }

  @Test void weeklyFeeIndoorIncludesSurcharge() {
    Cat c = cat(true, "Tunnel");
    c.checkIn(0);
    c.checkIn(1);
    assertEquals((Cat.BASE_DAILY_RATE + Cat.INDOOR_SURCHARGE) * 2, c.calculateWeeklyFee());
  }

  @Test void weeklyFeeOutdoorSkipsSurcharge() {
    Cat c = cat(false, "Tunnel");
    c.checkIn(0);
    c.checkIn(1);
    assertEquals(Cat.BASE_DAILY_RATE * 2, c.calculateWeeklyFee());
  }

  @Test void toStringIncludesIndoorAndToy() {
    Cat c = cat(true, "Feather Wand");
    String s = c.toString();
    assertTrue(s.contains("indoor=true"));
    assertTrue(s.contains("Feather Wand"));
  }

  @Test void inheritsMammalFields() {
    Cat c = cat(true, "Tunnel");
    assertEquals('F', c.getSex());
    assertTrue(c.isVaccinated());
    assertEquals(4.5, c.getWeight());
    assertTrue(c.isNeutered());
  }

  @Test void inheritsPetFields() {
    Owner o = new Owner(1, "Alice", "", "", "");
    Cat c = new Cat("Whiskers", 4, o, 0, 'F', true, 4.5, true, true, "Tunnel");
    assertEquals("Whiskers", c.getName());
    assertEquals(4, c.getAge());
    assertSame(o, c.getOwner());
  }

  @Test void chaos_allSixDaysIndoor() {
    Cat c = cat(true, "Tunnel");
    for (int i = 0; i < Pet.DAYS_PER_WEEK; i++) c.checkIn(i);
    assertEquals((Cat.BASE_DAILY_RATE + Cat.INDOOR_SURCHARGE) * 6, c.calculateWeeklyFee());
  }

  @Test void chaos_checkOutReducesFee() {
    Cat c = cat(false, "Tunnel");
    c.checkIn(0); c.checkIn(1);
    c.checkOut(0);
    assertEquals(Cat.BASE_DAILY_RATE, c.calculateWeeklyFee());
  }

  @Test void chaos_veryLongToyStringAccepted() {
    String longToy = "x".repeat(500);
    Cat c = cat(true, longToy);
    assertEquals(longToy, c.getFavouriteToy());
  }

  @Test void chaos_invalidMammalDefaultsCascade() {
    Cat c = new Cat("x", 0, null, 0, 'Z', false, 9999, false, true, null);
    assertEquals(Mammal.DEFAULT_SEX, c.getSex());
    assertEquals(Mammal.DEFAULT_WEIGHT, c.getWeight());
    assertEquals(Cat.DEFAULT_TOY, c.getFavouriteToy());
  }

  @Test void chaos_setFavouriteToyWithTabsAndNewlinesKept() {
    Cat c = cat(true, "orig");
    c.setFavouriteToy("\tFeather\n");
    assertEquals("\tFeather\n", c.getFavouriteToy());
  }
}
