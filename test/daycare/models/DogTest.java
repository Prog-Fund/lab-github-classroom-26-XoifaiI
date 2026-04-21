package daycare.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DogTest {

  private Dog dog(String breed, boolean dangerous) {
    return new Dog("Rex", 3, null, 0, 'M', true, 25, true, breed, dangerous);
  }

  @Test void constructorCanonicalisesKnownBreed() {
    Dog d = dog("labrador retriever", false);
    assertEquals("Labrador Retriever", d.getBreed());
  }

  @Test void constructorUsesDefaultForUnknownBreed() {
    Dog d = dog("Schnauzer", false);
    assertEquals(Dog.DEFAULT_BREED, d.getBreed());
  }

  @Test void constructorUsesDefaultForNullBreed() {
    Dog d = dog(null, false);
    assertEquals(Dog.DEFAULT_BREED, d.getBreed());
  }

  @Test void setBreedCanonicalisesKnown() {
    Dog d = dog("Beagle", false);
    d.setBreed("rottweiler");
    assertEquals("Rottweiler", d.getBreed());
  }

  @Test void setBreedIgnoresUnknown() {
    Dog d = dog("Beagle", false);
    d.setBreed("Schnauzer");
    assertEquals("Beagle", d.getBreed());
  }

  @Test void setBreedIgnoresNull() {
    Dog d = dog("Beagle", false);
    d.setBreed(null);
    assertEquals("Beagle", d.getBreed());
  }

  @Test void setDangerousBreedToggles() {
    Dog d = dog("Beagle", false);
    d.setDangerousBreed(true);
    assertTrue(d.isDangerousBreed());
  }

  @Test void weeklyFeeZeroDaysIsZero() {
    Dog d = dog("Beagle", false);
    assertEquals(0, d.calculateWeeklyFee());
  }

  @Test void weeklyFeeNonDangerous() {
    Dog d = dog("Beagle", false);
    d.checkIn(0);
    d.checkIn(1);
    d.checkIn(2);
    assertEquals(Dog.NONDANGEROUS_DAILY_RATE * 3, d.calculateWeeklyFee());
  }

  @Test void weeklyFeeDangerous() {
    Dog d = dog("Pit Bull", true);
    d.checkIn(0);
    d.checkIn(3);
    assertEquals(Dog.DANGEROUS_DAILY_RATE * 2, d.calculateWeeklyFee());
  }

  @Test void weeklyFeeFollowsDangerousFlagNotBreed() {
    Dog d = dog("Beagle", true);
    d.checkIn(1);
    assertEquals(Dog.DANGEROUS_DAILY_RATE, d.calculateWeeklyFee());
  }

  @Test void equalsOnBreedAndDangerousOnly() {
    Dog a = dog("Beagle", false);
    Dog b = dog("Beagle", false);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test void equalsRejectsDifferentBreed() {
    Dog a = dog("Beagle", false);
    Dog b = dog("Bulldog", false);
    assertNotEquals(a, b);
  }

  @Test void equalsRejectsDifferentDangerousFlag() {
    Dog a = dog("Beagle", false);
    Dog b = dog("Beagle", true);
    assertNotEquals(a, b);
  }

  @Test void equalsRejectsNullAndOtherTypes() {
    Dog a = dog("Beagle", false);
    assertNotEquals(null, a);
    assertNotEquals("dog", a);
  }

  @Test void equalsMatchesSelf() {
    Dog a = dog("Beagle", false);
    assertEquals(a, a);
  }

  @Test void toStringIncludesBreedAndFee() {
    Dog d = dog("Beagle", false);
    d.checkIn(0);
    String s = d.toString();
    assertTrue(s.contains("Beagle"));
    assertTrue(s.contains("30.00"));
  }

  @Test void chaos_allSixDaysMaxOutFee() {
    Dog d = dog("Pit Bull", true);
    for (int i = 0; i < Pet.DAYS_PER_WEEK; i++) d.checkIn(i);
    assertEquals(Dog.DANGEROUS_DAILY_RATE * 6, d.calculateWeeklyFee());
  }

  @Test void chaos_checkOutReducesFee() {
    Dog d = dog("Beagle", false);
    d.checkIn(0); d.checkIn(1);
    d.checkOut(1);
    assertEquals(Dog.NONDANGEROUS_DAILY_RATE, d.calculateWeeklyFee());
  }

  @Test void chaos_emptyBreedFallsBackToDefault() {
    Dog d = dog("", false);
    assertEquals(Dog.DEFAULT_BREED, d.getBreed());
  }

  @Test void chaos_whitespaceBreedFallsBackToDefault() {
    Dog d = dog("   ", false);
    assertEquals(Dog.DEFAULT_BREED, d.getBreed());
  }

  @Test void chaos_mixedCaseBreedCanonicalised() {
    Dog d = dog("gOlDeN ReTrIeVeR", false);
    assertEquals("Golden Retriever", d.getBreed());
  }
}
