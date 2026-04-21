package daycare.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OwnerTest {

  @Test void constructorStoresAllFields() {
    Owner o = new Owner(1, "Alice", "12 Main", "555-0100", "a@x.com");
    assertEquals(1, o.getId());
    assertEquals("Alice", o.getName());
    assertEquals("12 Main", o.getAddress());
    assertEquals("555-0100", o.getPhone());
    assertEquals("a@x.com", o.getEmail());
  }

  @Test void settersUpdateEveryField() {
    Owner o = new Owner(1, "Alice", "12 Main", "555-0100", "a@x.com");
    o.setId(2);
    o.setName("Bob");
    o.setAddress("1 Oak");
    o.setPhone("555-0200");
    o.setEmail("b@x.com");
    assertEquals(2, o.getId());
    assertEquals("Bob", o.getName());
    assertEquals("1 Oak", o.getAddress());
    assertEquals("555-0200", o.getPhone());
    assertEquals("b@x.com", o.getEmail());
  }

  @Test void toStringContainsIdAndName() {
    Owner o = new Owner(42, "Alice", "x", "y", "z");
    String s = o.toString();
    assertTrue(s.contains("42"));
    assertTrue(s.contains("Alice"));
  }

  @Test void chaos_nullsFlowThrough() {
    Owner o = new Owner(0, null, null, null, null);
    assertNull(o.getName());
    assertNull(o.getAddress());
    assertNull(o.getPhone());
    assertNull(o.getEmail());
  }

  @Test void chaos_emptyStringsAreKept() {
    Owner o = new Owner(0, "", "", "", "");
    assertEquals("", o.getName());
  }

  @Test void chaos_negativeIdAccepted() {
    Owner o = new Owner(-1, "a", "b", "c", "d");
    assertEquals(-1, o.getId());
  }

  @Test void chaos_toStringSurvivesNulls() {
    Owner o = new Owner(0, null, null, null, null);
    assertNotNull(o.toString());
  }
}
