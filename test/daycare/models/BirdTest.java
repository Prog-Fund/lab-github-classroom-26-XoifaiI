package daycare.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BirdTest {

  static class TestBird extends Bird {
    TestBird(double wingSpan, boolean canFly) {
      super("x", 1, null, 0, wingSpan, canFly);
    }
    @Override public double calculateWeeklyFee() { return 0; }
  }

  @Test void constructorStoresValidFields() {
    Bird b = new TestBird(50.5, true);
    assertEquals(50.5, b.getWingSpan());
    assertTrue(b.isCanFly());
  }

  @Test void wingSpanBelowMinFallsBackToDefault() {
    Bird b = new TestBird(Bird.MIN_WINGSPAN - 0.1, false);
    assertEquals(Bird.DEFAULT_WINGSPAN, b.getWingSpan());
  }

  @Test void wingSpanAboveMaxFallsBackToDefault() {
    Bird b = new TestBird(Bird.MAX_WINGSPAN + 1, false);
    assertEquals(Bird.DEFAULT_WINGSPAN, b.getWingSpan());
  }

  @Test void wingSpanAtBoundariesIsValid() {
    assertEquals(Bird.MIN_WINGSPAN, new TestBird(Bird.MIN_WINGSPAN, false).getWingSpan());
    assertEquals(Bird.MAX_WINGSPAN, new TestBird(Bird.MAX_WINGSPAN, false).getWingSpan());
  }

  @Test void setWingSpanIgnoresOutOfRange() {
    Bird b = new TestBird(50, false);
    b.setWingSpan(-5);
    assertEquals(50, b.getWingSpan());
  }

  @Test void setWingSpanUpdatesOnValid() {
    Bird b = new TestBird(50, false);
    b.setWingSpan(120);
    assertEquals(120, b.getWingSpan());
  }

  @Test void setCanFlyToggles() {
    Bird b = new TestBird(50, false);
    b.setCanFly(true);
    assertTrue(b.isCanFly());
  }

  @Test void equalsMatchesOnWingSpanAndCanFly() {
    Bird a = new TestBird(50, true);
    Bird b = new TestBird(50, true);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test void equalsRejectsDifferentWingSpan() {
    Bird a = new TestBird(50, true);
    Bird b = new TestBird(75, true);
    assertNotEquals(a, b);
  }

  @Test void equalsRejectsDifferentCanFly() {
    Bird a = new TestBird(50, true);
    Bird b = new TestBird(50, false);
    assertNotEquals(a, b);
  }

  @Test void equalsRejectsNullAndOtherTypes() {
    Bird a = new TestBird(50, true);
    assertNotEquals(null, a);
    assertNotEquals("bird", a);
  }

  @Test void equalsMatchesSelf() {
    Bird a = new TestBird(50, true);
    assertEquals(a, a);
  }

  @Test void toStringIncludesFields() {
    Bird b = new TestBird(155.5, true);
    String s = b.toString();
    assertTrue(s.contains("155.50"));
    assertTrue(s.contains("canFly=true"));
  }

  @Test void chaos_nanWingSpanRejected() {
    Bird b = new TestBird(Double.NaN, false);
    assertEquals(Bird.DEFAULT_WINGSPAN, b.getWingSpan());
  }

  @Test void chaos_infinityWingSpanRejected() {
    Bird b = new TestBird(Double.POSITIVE_INFINITY, false);
    assertEquals(Bird.DEFAULT_WINGSPAN, b.getWingSpan());
  }

  @Test void chaos_negativeWingSpanRejected() {
    Bird b = new TestBird(-100, false);
    assertEquals(Bird.DEFAULT_WINGSPAN, b.getWingSpan());
  }
}
