package daycare.controllers;

import static org.junit.jupiter.api.Assertions.*;

import daycare.models.Owner;
import java.io.File;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OwnerAPITest {

  private OwnerAPI api;

  @BeforeEach void setUp() {
    api = new OwnerAPI("daycare-owners", new File("build/tmp/owners.xml"));
  }

  @Test void emptyApiStartsAtZero() {
    assertEquals(0, api.numberOfOwners());
    assertTrue(api.getOwners().isEmpty());
  }

  @Test void addOwnerByFieldsAssignsIdAndStores() {
    Owner alice = api.addOwner("Alice", "12 Main", "555-0100", "a@x.com");
    assertEquals(1, alice.getId());
    assertEquals("Alice", alice.getName());
    assertEquals(1, api.numberOfOwners());
  }

  @Test void addOwnerByFieldsAssignsSequentialIds() {
    Owner alice = api.addOwner("Alice", "", "", "");
    Owner bob = api.addOwner("Bob", "", "", "");
    Owner carol = api.addOwner("Carol", "", "", "");
    assertEquals(1, alice.getId());
    assertEquals(2, bob.getId());
    assertEquals(3, carol.getId());
  }

  @Test void addOwnerObjectWithIdZeroAssignsFresh() {
    Owner zero = new Owner(0, "Alice", "", "", "");
    assertTrue(api.addOwner(zero));
    assertEquals(1, zero.getId());
  }

  @Test void addOwnerObjectKeepsExplicitIdAndAdvancesCounter() {
    Owner explicit = new Owner(50, "Alice", "", "", "");
    assertTrue(api.addOwner(explicit));
    assertEquals(50, explicit.getId());

    Owner next = api.addOwner("Bob", "", "", "");
    assertEquals(51, next.getId());
  }

  @Test void addOwnerObjectNullRejected() {
    assertFalse(api.addOwner((Owner) null));
    assertEquals(0, api.numberOfOwners());
  }

  @Test void removeOwnerReturnsRemoved() {
    Owner alice = api.addOwner("Alice", "", "", "");
    assertSame(alice, api.removeOwner(0));
    assertEquals(0, api.numberOfOwners());
  }

  @Test void removeOwnerInvalidIndexReturnsNull() {
    assertNull(api.removeOwner(0));
    assertNull(api.removeOwner(-1));
    api.addOwner("Alice", "", "", "");
    assertNull(api.removeOwner(5));
  }

  @Test void getOwnerByIndexReturnsNullOnInvalid() {
    assertNull(api.getOwner(-1));
    assertNull(api.getOwner(0));
    api.addOwner("Alice", "", "", "");
    assertNull(api.getOwner(1));
  }

  @Test void getOwnerByIndexReturnsOwner() {
    Owner alice = api.addOwner("Alice", "", "", "");
    assertSame(alice, api.getOwner(0));
  }

  @Test void getOwnerByNameCaseInsensitive() {
    Owner alice = api.addOwner("Alice", "", "", "");
    assertSame(alice, api.getOwnerByName("alice"));
    assertSame(alice, api.getOwnerByName("ALICE"));
  }

  @Test void getOwnerByNameMissingReturnsNull() {
    assertNull(api.getOwnerByName("Ghost"));
  }

  @Test void getOwnerByIdReturnsOwner() {
    Owner alice = api.addOwner("Alice", "", "", "");
    assertSame(alice, api.getOwnerById(alice.getId()));
  }

  @Test void getOwnerByIdMissingReturnsNull() {
    assertNull(api.getOwnerById(99999));
  }

  @Test void isValidOwnerIndex() {
    api.addOwner("Alice", "", "", "");
    assertTrue(api.isValidOwnerIndex(0));
    assertFalse(api.isValidOwnerIndex(-1));
    assertFalse(api.isValidOwnerIndex(1));
  }

  @Test void numberOfOwnersTracksCount() {
    assertEquals(0, api.numberOfOwners());
    api.addOwner("Alice", "", "", "");
    api.addOwner("Bob", "", "", "");
    assertEquals(2, api.numberOfOwners());
    api.removeOwner(0);
    assertEquals(1, api.numberOfOwners());
  }

  @Test void getOwnersReturnsDefensiveCopy() {
    api.addOwner("Alice", "", "", "");
    ArrayList<Owner> got = api.getOwners();
    got.clear();
    assertEquals(1, api.numberOfOwners());
  }

  @Test void listAllOwnersEmptyFallback() {
    assertEquals("(no owners)", api.listAllOwners());
  }

  @Test void listAllOwnersIndexed() {
    api.addOwner("Alice", "", "", "");
    api.addOwner("Bob", "", "", "");
    String listed = api.listAllOwners();
    assertTrue(listed.contains("0:"));
    assertTrue(listed.contains("Alice"));
    assertTrue(listed.contains("1:"));
    assertTrue(listed.contains("Bob"));
  }

  @Test void nameGetterAndSetter() {
    assertEquals("daycare-owners", api.getName());
    api.setName("new-name");
    assertEquals("new-name", api.getName());
  }

  @Test void fileNameReflectsConstructorFile() {
    assertEquals("owners.xml", api.fileName());
  }

  @Test void chaos_addOwnerObjectBelowCounterStillAccepted() {
    api.addOwner("Alice", "", "", "");
    Owner low = new Owner(1, "Bob", "", "", "");
    assertTrue(api.addOwner(low));
    assertEquals(2, api.numberOfOwners());
  }

  @Test void chaos_getOwnerByNameReturnsFirstMatch() {
    Owner first = api.addOwner("Alice", "", "", "");
    api.addOwner("Alice", "", "", "");
    assertSame(first, api.getOwnerByName("Alice"));
  }

  @Test void chaos_removeShiftsRemainingIndexes() {
    api.addOwner("Alice", "", "", "");
    Owner bob = api.addOwner("Bob", "", "", "");
    api.removeOwner(0);
    assertSame(bob, api.getOwner(0));
  }

  @Test void chaos_unicodeOwnerNameRoundtripsThroughLookup() {
    Owner yuki = api.addOwner("Yukié", "", "", "");
    assertSame(yuki, api.getOwnerByName("yukié"));
  }

  @Test void chaos_largeExplicitIdAdvancesCounter() {
    api.addOwner(new Owner(1_000_000, "Big", "", "", ""));
    Owner next = api.addOwner("Next", "", "", "");
    assertEquals(1_000_001, next.getId());
  }
}
