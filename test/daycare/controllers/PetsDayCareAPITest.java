package daycare.controllers;

import static org.junit.jupiter.api.Assertions.*;

import daycare.models.Cat;
import daycare.models.Dog;
import daycare.models.Owner;
import daycare.models.Parrot;
import daycare.models.Pet;
import java.io.File;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PetsDayCareAPITest {

  private PetsDayCareAPI api;
  private Owner alice;
  private Owner bob;

  @BeforeEach void setUp() {
    api = new PetsDayCareAPI("kennels", 10, new File("build/tmp/pets.xml"));
    alice = new Owner(1, "Alice", "", "", "");
    bob = new Owner(2, "Bob", "", "", "");
  }

  private Dog dog(String name, Owner o, String breed, boolean dangerous) {
    return new Dog(name, 3, o, 0, 'M', true, 25, true, breed, dangerous);
  }

  private Cat indoorCat(String name, Owner o) {
    return new Cat(name, 3, o, 0, 'F', true, 4, true, true, "Tunnel");
  }

  private Cat outdoorCat(String name, Owner o) {
    return new Cat(name, 3, o, 0, 'F', true, 4, true, false, "Tunnel");
  }

  private Parrot parrot(String name, Owner o, int vocab) {
    return new Parrot(name, 3, o, 0, 50, true, vocab);
  }

  @Test void emptyApiStartsAtZero() {
    assertEquals(0, api.numberOfPets());
    assertEquals(0, api.numberOfDogs());
    assertEquals(0, api.numberOfCats());
    assertEquals(0, api.numberOfParrots());
  }

  @Test void addPetAddsAndReturnsTrue() {
    assertTrue(api.addPet(dog("Rex", alice, "Beagle", false)));
    assertEquals(1, api.numberOfPets());
  }

  @Test void addPetNullRejected() {
    assertFalse(api.addPet(null));
    assertEquals(0, api.numberOfPets());
  }

  @Test void addPetAtCapacityRejected() {
    PetsDayCareAPI tiny = new PetsDayCareAPI("k", 1, new File("build/tmp/pets.xml"));
    tiny.addPet(dog("Rex", alice, "Beagle", false));
    assertFalse(tiny.addPet(dog("Bear", alice, "Beagle", false)));
  }

  @Test void deletePetByIndexRemovesAndReturns() {
    Dog rex = dog("Rex", alice, "Beagle", false);
    api.addPet(rex);
    assertSame(rex, api.deletePetByIndex(0));
    assertEquals(0, api.numberOfPets());
  }

  @Test void deletePetByIndexOutOfRangeReturnsNull() {
    assertNull(api.deletePetByIndex(0));
    assertNull(api.deletePetByIndex(-1));
  }

  @Test void deletePetByIdFindsAndRemoves() {
    Dog rex = dog("Rex", alice, "Beagle", false);
    api.addPet(rex);
    Pet removed = api.deletePetById(rex.getId());
    assertSame(rex, removed);
    assertEquals(0, api.numberOfPets());
  }

  @Test void deletePetByIdMissingReturnsNull() {
    assertNull(api.deletePetById(99999));
  }

  @Test void updatePetReplacesAtIndex() {
    Dog rex = dog("Rex", alice, "Beagle", false);
    Dog bear = dog("Bear", bob, "Bulldog", false);
    api.addPet(rex);
    assertSame(bear, api.updatePet(0, bear));
    assertSame(bear, api.getPet(0));
  }

  @Test void updatePetInvalidIndexReturnsNull() {
    assertNull(api.updatePet(99, dog("x", alice, "Beagle", false)));
  }

  @Test void updatePetNullReturnsNull() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    assertNull(api.updatePet(0, null));
  }

  @Test void getPetByIndexReturnsNullOnInvalid() {
    assertNull(api.getPet(-1));
    assertNull(api.getPet(5));
  }

  @Test void getPetByNameCaseInsensitive() {
    Dog rex = dog("Rex", alice, "Beagle", false);
    api.addPet(rex);
    assertSame(rex, api.getPet("rex"));
    assertSame(rex, api.getPet("REX"));
    assertNull(api.getPet("Missing"));
  }

  @Test void getPetByIdReturnsPet() {
    Dog rex = dog("Rex", alice, "Beagle", false);
    api.addPet(rex);
    assertSame(rex, api.getPetById(rex.getId()));
    assertNull(api.getPetById(0));
  }

  @Test void isValidPetIndex() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    assertTrue(api.isValidPetIndex(0));
    assertFalse(api.isValidPetIndex(-1));
    assertFalse(api.isValidPetIndex(1));
  }

  @Test void countsByType() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    api.addPet(dog("Fang", bob, "Rottweiler", true));
    api.addPet(indoorCat("Whiskers", alice));
    api.addPet(outdoorCat("Tom", bob));
    api.addPet(parrot("Polly", alice, 50));

    assertEquals(5, api.numberOfPets());
    assertEquals(2, api.numberOfDogs());
    assertEquals(1, api.numberOfDangerousDogs());
    assertEquals(2, api.numberOfCats());
    assertEquals(1, api.numberOfIndoorCats());
    assertEquals(1, api.numberOfParrots());
  }

  @Test void numberOfParrotsByVocabularySizeGroupsByTier() {
    api.addPet(parrot("P1", alice, 5));    // Basic
    api.addPet(parrot("P2", alice, 15));   // Intermediate
    api.addPet(parrot("P3", alice, 45));   // Advanced
    api.addPet(parrot("P4", alice, 80));   // Amazing
    api.addPet(parrot("P5", alice, 80));   // Amazing
    assertEquals(2, api.numberOfParrotsByVocabularySize(70));
    assertEquals(1, api.numberOfParrotsByVocabularySize(35));
    assertEquals(1, api.numberOfParrotsByVocabularySize(0));
  }

  @Test void listAllPetsShowsIndexed() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    String listed = api.listAllPets();
    assertTrue(listed.contains("0:"));
    assertTrue(listed.contains("Rex"));
  }

  @Test void listAllPetsEmptyMessage() {
    assertEquals("No Pets", api.listAllPets());
  }

  @Test void listAllDogsOnlyDogs() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    api.addPet(indoorCat("Whiskers", alice));
    String listed = api.listAllDogs();
    assertTrue(listed.contains("Rex"));
    assertFalse(listed.contains("Whiskers"));
  }

  @Test void listAllDogsEmptyMessage() {
    assertEquals("No Dogs", api.listAllDogs());
  }

  @Test void listAllCatsEmptyMessage() {
    assertEquals("No cats", api.listAllCats());
  }

  @Test void listAllParrotsEmptyMessage() {
    assertEquals("No Parrots", api.listAllParrots());
  }

  @Test void listAllDangerousDogsEmptyMessage() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    assertEquals("No Dangerous Dogs in the Kennels", api.listAllDangerousDogs());
  }

  @Test void listAllDangerousDogsShowsOnlyDangerous() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    api.addPet(dog("Fang", bob, "Rottweiler", true));
    String listed = api.listAllDangerousDogs();
    assertFalse(listed.contains("Rex"));
    assertTrue(listed.contains("Fang"));
  }

  @Test void listAllPetsByOwnerFiltersByName() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    api.addPet(dog("Bear", bob, "Bulldog", false));
    String listed = api.listAllPetsByOwner("alice");
    assertTrue(listed.contains("Rex"));
    assertFalse(listed.contains("Bear"));
  }

  @Test void listAllPetsByOwnerEmptyMessageIncludesName() {
    assertTrue(api.listAllPetsByOwner("Ghost").contains("Ghost"));
  }

  @Test void listAllPetsThatStayMoreThanDays() {
    Dog rex = dog("Rex", alice, "Beagle", false);
    rex.checkIn(0); rex.checkIn(1); rex.checkIn(2); rex.checkIn(3);
    Dog bear = dog("Bear", bob, "Bulldog", false);
    bear.checkIn(0);
    api.addPet(rex);
    api.addPet(bear);

    String listed = api.listAllPetsThatStayMoreThanDays(2);
    assertTrue(listed.contains("Rex"));
    assertFalse(listed.contains("Bear"));
  }

  @Test void listAllPetsThatStayMoreThanDaysEmptyMessage() {
    assertTrue(api.listAllPetsThatStayMoreThanDays(3).contains("3"));
  }

  @Test void listOwnersEmptyFallback() {
    assertEquals("(no owners)", api.listOwners());
  }

  @Test void listOwnersDistinct() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    api.addPet(dog("Bear", alice, "Bulldog", false));
    api.addPet(indoorCat("Whiskers", bob));
    String owners = api.listOwners();
    assertTrue(owners.contains("Alice"));
    assertTrue(owners.contains("Bob"));
    assertEquals(owners.indexOf("Alice"), owners.lastIndexOf("Alice"));
  }

  @Test void findDogByOwnerAndBreedAndAgeMatch() {
    Dog rex = new Dog("Rex", 3, alice, 0, 'M', true, 25, true, "Beagle", false);
    api.addPet(rex);
    assertSame(rex, api.findDogByOwnerAndBreedAndAge("alice", "beagle", 3));
  }

  @Test void findDogByOwnerAndBreedAndAgeNoMatchReturnsNull() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    assertNull(api.findDogByOwnerAndBreedAndAge("alice", "beagle", 99));
    assertNull(api.findDogByOwnerAndBreedAndAge("alice", "Schnauzer", 3));
    assertNull(api.findDogByOwnerAndBreedAndAge("ghost", "beagle", 3));
    assertNull(api.findDogByOwnerAndBreedAndAge("alice", null, 3));
  }

  @Test void getPetsByOwnersNameJoinsWithComma() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    api.addPet(indoorCat("Whiskers", alice));
    String list = api.getPetsByOwnersName("Alice");
    assertTrue(list.contains("Rex"));
    assertTrue(list.contains("Whiskers"));
    assertTrue(list.contains(", "));
  }

  @Test void getPetsByOwnersNameEmptyMessage() {
    assertTrue(api.getPetsByOwnersName("Ghost").contains("Ghost"));
  }

  @Test void sortPetsByNameAscending() {
    api.addPet(dog("Charlie", alice, "Beagle", false));
    api.addPet(dog("alpha", alice, "Beagle", false));
    api.addPet(dog("Bravo", alice, "Beagle", false));
    api.sortPetsByName();
    assertEquals("alpha", api.getPet(0).getName());
    assertEquals("Bravo", api.getPet(1).getName());
    assertEquals("Charlie", api.getPet(2).getName());
  }

  @Test void sortPetsByIdDescending() {
    Dog first = new Dog("A", 1, alice, 2000, 'M', true, 25, true, "Beagle", false);
    Dog second = new Dog("B", 1, alice, 4000, 'M', true, 25, true, "Beagle", false);
    Dog third = new Dog("C", 1, alice, 3000, 'M', true, 25, true, "Beagle", false);
    api.addPet(first);
    api.addPet(second);
    api.addPet(third);
    api.sortPetsById();
    assertEquals(4000, api.getPet(0).getId());
    assertEquals(3000, api.getPet(1).getId());
    assertEquals(2000, api.getPet(2).getId());
  }

  @Test void weeklyIncomeSumsAllPets() {
    Dog rex = dog("Rex", alice, "Beagle", false);
    rex.checkIn(0);
    Dog fang = dog("Fang", bob, "Pit Bull", true);
    fang.checkIn(0); fang.checkIn(1);
    api.addPet(rex);
    api.addPet(fang);
    assertEquals(Dog.NONDANGEROUS_DAILY_RATE + Dog.DANGEROUS_DAILY_RATE * 2,
        api.getWeeklyIncome());
  }

  @Test void weeklyIncomeEmptyIsZero() {
    assertEquals(0, api.getWeeklyIncome());
  }

  @Test void averageDaysPerWeekEmptyIsZero() {
    assertEquals(0, api.getAverageNumDaysPerWeek());
  }

  @Test void averageDaysPerWeekDivides() {
    Dog rex = dog("Rex", alice, "Beagle", false);
    rex.checkIn(0); rex.checkIn(1);
    Dog bear = dog("Bear", bob, "Bulldog", false);
    bear.checkIn(0);
    api.addPet(rex);
    api.addPet(bear);
    assertEquals(1.5, api.getAverageNumDaysPerWeek());
  }

  @Test void nameAndMaxGettersAndSetters() {
    assertEquals("kennels", api.getName());
    api.setName("new");
    assertEquals("new", api.getName());
    api.setName(null);
    assertEquals("new", api.getName());

    assertEquals(10, api.getMaxNumberOfPets());
    api.setMaxNumberOfPets(5);
    assertEquals(5, api.getMaxNumberOfPets());
    api.setMaxNumberOfPets(-1);
    assertEquals(5, api.getMaxNumberOfPets());
  }

  @Test void initNameMatchesSetName() {
    api.initName("init");
    assertEquals("init", api.getName());
    api.initName(null);
    assertEquals("init", api.getName());
  }

  @Test void getPetsArrayReturnsDefensiveCopy() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    ArrayList<Pet> got = api.getPetsArray();
    got.clear();
    assertEquals(1, api.numberOfPets());
  }

  @Test void setPetsArrayReplacesAndCopies() {
    ArrayList<Pet> fresh = new ArrayList<>();
    fresh.add(dog("Rex", alice, "Beagle", false));
    api.setPetsArray(fresh);
    fresh.clear();
    assertEquals(1, api.numberOfPets());
  }

  @Test void setPetsArrayIgnoresNull() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    api.setPetsArray(null);
    assertEquals(1, api.numberOfPets());
  }

  @Test void fileNameReflectsConstructorFile() {
    assertEquals("pets.xml", api.fileName());
  }

  @Test void nameTruncatedToMax() {
    PetsDayCareAPI big = new PetsDayCareAPI("x".repeat(500), 10, new File("x"));
    assertEquals(PetsDayCareAPI.MAX_NAME_LENGTH, big.getName().length());
  }

  @Test void chaos_sortEmptyListIsNoop() {
    api.sortPetsByName();
    api.sortPetsById();
    assertEquals(0, api.numberOfPets());
  }

  @Test void chaos_sortSingletonIsNoop() {
    api.addPet(dog("Rex", alice, "Beagle", false));
    api.sortPetsByName();
    api.sortPetsById();
    assertEquals(1, api.numberOfPets());
  }

  @Test void chaos_capacityZeroRejectsEverything() {
    PetsDayCareAPI none = new PetsDayCareAPI("k", 0, new File("x"));
    assertFalse(none.addPet(dog("Rex", alice, "Beagle", false)));
  }

  @Test void chaos_listWithUnicodeOwnerNameMatches() {
    Owner yuki = new Owner(99, "Yukié", "", "", "");
    api.addPet(dog("Inu", yuki, "Beagle", false));
    assertTrue(api.listAllPetsByOwner("Yukié").contains("Inu"));
  }

  @Test void chaos_sortStableWithTies() {
    Dog a = new Dog("Same", 1, alice, 2000, 'M', true, 25, true, "Beagle", false);
    Dog b = new Dog("Same", 1, alice, 3000, 'M', true, 25, true, "Beagle", false);
    api.addPet(a);
    api.addPet(b);
    api.sortPetsByName();
    assertEquals(2, api.numberOfPets());
  }

  @Test void chaos_sortReverseOrderEndsSortedAscending() {
    for (int i = 5; i >= 1; i--) {
      api.addPet(dog(String.valueOf((char) ('A' + i)), alice, "Beagle", false));
    }
    api.sortPetsByName();
    for (int i = 0; i < api.numberOfPets() - 1; i++) {
      assertTrue(api.getPet(i).getName().compareTo(api.getPet(i + 1).getName()) <= 0);
    }
  }

  @Test void chaos_findDogRejectsNullOwnerOnPet() {
    api.addPet(dog("Orphan", null, "Beagle", false));
    assertNull(api.findDogByOwnerAndBreedAndAge("anyone", "beagle", 3));
  }

  @Test void chaos_listAllPetsByOwnerSkipsNullOwners() {
    api.addPet(dog("Orphan", null, "Beagle", false));
    assertTrue(api.listAllPetsByOwner("anyone").contains("No Pet"));
  }

  @Test void chaos_deletePetByIdDoesNotConfuseWithIndex() {
    Dog rex = dog("Rex", alice, "Beagle", false);
    api.addPet(rex);
    assertNull(api.deletePetById(0));
    assertEquals(1, api.numberOfPets());
  }
}
