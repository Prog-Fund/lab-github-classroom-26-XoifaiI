package daycare.models;

import static org.junit.jupiter.api.Assertions.*;

import daycare.utils.BirdUtility;
import org.junit.jupiter.api.Test;

class ParrotTest {

  private Parrot parrot(double wingSpan, boolean canFly, int vocab) {
    return new Parrot("Polly", 5, null, 0, wingSpan, canFly, vocab);
  }

  @Test void constructorCategorisesVocabulary() {
    assertEquals("Basic", parrot(50, true, 0).getVocabularySize());
    assertEquals("Basic", parrot(50, true, 9).getVocabularySize());
    assertEquals("Intermediate", parrot(50, true, 10).getVocabularySize());
    assertEquals("Intermediate", parrot(50, true, 29).getVocabularySize());
    assertEquals("Advanced", parrot(50, true, 30).getVocabularySize());
    assertEquals("Advanced", parrot(50, true, 59).getVocabularySize());
    assertEquals("Amazing", parrot(50, true, 60).getVocabularySize());
  }

  @Test void negativeVocabFallsBackToDefault() {
    Parrot p = parrot(50, true, -1);
    assertEquals(BirdUtility.DEFAULT_VOCAB_CATEGORY, p.getVocabularySize());
  }

  @Test void setVocabularySizeReCategorises() {
    Parrot p = parrot(50, true, 5);
    p.setVocabularySize(100);
    assertEquals("Amazing", p.getVocabularySize());
  }

  @Test void setVocabularySizeNegativeGoesToDefault() {
    Parrot p = parrot(50, true, 5);
    p.setVocabularySize(-99);
    assertEquals(BirdUtility.DEFAULT_VOCAB_CATEGORY, p.getVocabularySize());
  }

  @Test void weeklyFeeZeroDaysIsZero() {
    assertEquals(0, parrot(50, true, 5).calculateWeeklyFee());
  }

  @Test void weeklyFeeScalesWithDays() {
    Parrot p = parrot(50, true, 5);
    p.checkIn(0); p.checkIn(2); p.checkIn(4);
    assertEquals(Parrot.BASE_DAILY_RATE * 3, p.calculateWeeklyFee());
  }

  @Test void inheritsBirdFields() {
    Parrot p = parrot(155.5, true, 5);
    assertEquals(155.5, p.getWingSpan());
    assertTrue(p.isCanFly());
  }

  @Test void inheritsPetFields() {
    Owner o = new Owner(1, "Bob", "", "", "");
    Parrot p = new Parrot("Polly", 5, o, 0, 50, true, 20);
    assertEquals("Polly", p.getName());
    assertEquals(5, p.getAge());
    assertSame(o, p.getOwner());
  }

  @Test void toStringIncludesVocabAndFee() {
    Parrot p = parrot(50, true, 45);
    p.checkIn(0);
    String s = p.toString();
    assertTrue(s.contains("Advanced"));
    assertTrue(s.contains("10.00"));
  }

  @Test void chaos_invalidBirdDefaultsCascade() {
    Parrot p = parrot(-50, false, 5);
    assertEquals(Bird.DEFAULT_WINGSPAN, p.getWingSpan());
  }

  @Test void chaos_hugeVocabStillAmazing() {
    assertEquals("Amazing", parrot(50, true, Integer.MAX_VALUE).getVocabularySize());
  }

  @Test void chaos_minIntVocabGoesToDefault() {
    Parrot p = parrot(50, true, Integer.MIN_VALUE);
    assertEquals(BirdUtility.DEFAULT_VOCAB_CATEGORY, p.getVocabularySize());
  }

  @Test void chaos_allSixDaysMaxOutFee() {
    Parrot p = parrot(50, true, 5);
    for (int i = 0; i < Pet.DAYS_PER_WEEK; i++) p.checkIn(i);
    assertEquals(Parrot.BASE_DAILY_RATE * 6, p.calculateWeeklyFee());
  }

  @Test void chaos_checkOutReducesFee() {
    Parrot p = parrot(50, true, 5);
    p.checkIn(0); p.checkIn(1);
    p.checkOut(1);
    assertEquals(Parrot.BASE_DAILY_RATE, p.calculateWeeklyFee());
  }

  @Test void chaos_boundaryValuesExactMatch() {
    assertEquals("Amazing", parrot(50, true, 60).getVocabularySize());
    assertEquals("Advanced", parrot(50, true, 59).getVocabularySize());
    assertEquals("Advanced", parrot(50, true, 30).getVocabularySize());
    assertEquals("Intermediate", parrot(50, true, 29).getVocabularySize());
  }
}
