package daycare.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MammalTest {

  static class TestMammal extends Mammal {
    TestMammal(char sex, boolean vaccinated, double weight, boolean neutered) {
      super("x", 1, null, 0, sex, vaccinated, weight, neutered);
    }
    @Override public double calculateWeeklyFee() { return 0; }
  }

  @Test void constructorStoresValidFields() {
    Mammal m = new TestMammal('F', true, 25.5, true);
    assertEquals('F', m.getSex());
    assertTrue(m.isVaccinated());
    assertEquals(25.5, m.getWeight());
    assertTrue(m.isNeutered());
  }

  @Test void invalidSexFallsBackToDefault() {
    Mammal m = new TestMammal('Z', false, 10, false);
    assertEquals(Mammal.DEFAULT_SEX, m.getSex());
  }

  @Test void lowerCaseSexAcceptedViaCanonicalisation() {
    Mammal m = new TestMammal('f', false, 10, false);
    assertEquals('f', m.getSex());
  }

  @Test void weightBelowMinFallsBackToDefault() {
    Mammal m = new TestMammal('M', false, Mammal.MIN_WEIGHT - 0.1, false);
    assertEquals(Mammal.DEFAULT_WEIGHT, m.getWeight());
  }

  @Test void weightAboveMaxFallsBackToDefault() {
    Mammal m = new TestMammal('M', false, Mammal.MAX_WEIGHT + 1, false);
    assertEquals(Mammal.DEFAULT_WEIGHT, m.getWeight());
  }

  @Test void weightAtBoundariesIsValid() {
    assertEquals(Mammal.MIN_WEIGHT, new TestMammal('M', false, Mammal.MIN_WEIGHT, false).getWeight());
    assertEquals(Mammal.MAX_WEIGHT, new TestMammal('M', false, Mammal.MAX_WEIGHT, false).getWeight());
  }

  @Test void setSexIgnoresInvalid() {
    Mammal m = new TestMammal('F', false, 10, false);
    m.setSex('Z');
    assertEquals('F', m.getSex());
  }

  @Test void setSexUpdatesOnValid() {
    Mammal m = new TestMammal('F', false, 10, false);
    m.setSex('U');
    assertEquals('U', m.getSex());
  }

  @Test void setWeightIgnoresOutOfRange() {
    Mammal m = new TestMammal('M', false, 50, false);
    m.setWeight(9999);
    assertEquals(50, m.getWeight());
    m.setWeight(-1);
    assertEquals(50, m.getWeight());
  }

  @Test void setWeightUpdatesOnValid() {
    Mammal m = new TestMammal('M', false, 50, false);
    m.setWeight(75);
    assertEquals(75, m.getWeight());
  }

  @Test void setVaccinatedAndSetNeuteredToggle() {
    Mammal m = new TestMammal('M', false, 50, false);
    m.setVaccinated(true);
    m.setNeutered(true);
    assertTrue(m.isVaccinated());
    assertTrue(m.isNeutered());
  }

  @Test void toStringIncludesAllFields() {
    Mammal m = new TestMammal('F', true, 12.5, false);
    String s = m.toString();
    assertTrue(s.contains("sex=F"));
    assertTrue(s.contains("vaccinated=true"));
    assertTrue(s.contains("neutered=false"));
    assertTrue(s.contains("12.50"));
  }

  @Test void chaos_unicodeSexFallsBackToDefault() {
    Mammal m = new TestMammal('é', false, 10, false);
    assertEquals(Mammal.DEFAULT_SEX, m.getSex());
  }

  @Test void chaos_nanWeightRejectedByRangeCheck() {
    Mammal m = new TestMammal('M', false, Double.NaN, false);
    assertEquals(Mammal.DEFAULT_WEIGHT, m.getWeight());
  }

  @Test void chaos_infinityWeightRejected() {
    Mammal m = new TestMammal('M', false, Double.POSITIVE_INFINITY, false);
    assertEquals(Mammal.DEFAULT_WEIGHT, m.getWeight());
  }

  @Test void chaos_setSexToZeroCharIgnored() {
    Mammal m = new TestMammal('M', false, 10, false);
    m.setSex('\0');
    assertEquals('M', m.getSex());
  }
}
