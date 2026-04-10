package daycare.controllers;

import daycare.models.Bird;
import daycare.models.Cat;
import daycare.models.Dog;
import daycare.models.Mammal;
import daycare.models.Owner;
import daycare.models.Parrot;
import daycare.models.Pet;
import daycare.utils.BirdUtility;
import daycare.utils.ISerializer;
import daycare.utils.Utilities;
import daycare.utils.XmlSerializer;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PetsDayCareAPI: in memory CRUD + queries + reports for the daycares pet list.
 *
 * <p>this is the workhorse controller. it owns an {@code ArrayList<Pet>},
 * exposes count/list/sort/search helpers used by the menu and reports
 * screens, and persists the whole list to XML via {@link XmlSerializer}.
 *
 * <p>id assignment lives on {@link Pet} (static counter starting at 1000), not
 * here. callers can pass id = 0 to a Pet ctor and it will hand out a fresh
 * one. this class never touches ids.
 *
 * <p>note: most listing methods return a single newline delimited String so
 * the Driver can pipe them straight to {@code System.out.println}. zero
 * formatting smarts in here, the menu/Tui layer is in charge of styling.
 *
 * <p>sorting: hand rolled bubble sort that calls {@link #swapPets(int, int)}
 * (private). spec is explicit about not using library sort. sortPetsById is
 * descending, sortPetsByName is ascending.
 *
 * <p>Returns: instance, construct with name + max capacity + target file.
 *
 * <p>Example:
 * <pre>{@code
 * PetsDayCareAPI api = new PetsDayCareAPI("daycare", 100, new File("data/pets.xml"));
 * api.load();
 * Owner alice = ...;
 * Dog rex = new Dog("Rex", 3, alice, 0,
 *     'M', true, 25.5, true, "Labrador Retriever", false);
 * api.addPet(rex);
 * rex.checkIn(0);
 * rex.checkIn(1);
 * double income = api.getWeeklyIncome();
 * api.save();
 * }</pre>
 */
public class PetsDayCareAPI implements ISerializer {

  /** name field is capped at 20 chars per spec, anything longer gets truncated. */
  public static final int MAX_NAME_LENGTH = 20;

  /**
   * xstream security allowlist for {@link #load()}. every concrete class that
   * can appear in the saved pet graph (incl. the ArrayList wrapper and the
   * primitive boolean[] used for Pet.daysAttending) has to be listed here or
   * xstream will refuse to deserialise it. dont add anything risky (Runtime,
   * ProcessBuilder, JdbcRowSetImpl, etc) to this list ever, thats exactly the
   * gadget chain surface xstreams allowTypes is meant to block.
   */
  private static final Class<?>[] ALLOWED_TYPES = {
      Pet.class, Mammal.class, Bird.class,
      Dog.class, Cat.class, Parrot.class,
      Owner.class, ArrayList.class, boolean[].class
  };

  private String name;
  private int maxNumberOfPets;
  private File file;
  private ArrayList<Pet> pets;

  public PetsDayCareAPI(String name, int maxNumberOfPets, File file) {
    this.name = Utilities.truncate(name, MAX_NAME_LENGTH);
    this.maxNumberOfPets = maxNumberOfPets;
    this.file = file;
    this.pets = new ArrayList<>();
  }

  public boolean addPet(Pet pet) {
    if (pet == null) {
      return false;
    }
    if (pets.size() >= maxNumberOfPets) {
      return false;
    }
    return pets.add(pet);
  }

  public Pet deletePetByIndex(int index) {
    if (!isValidPetIndex(index)) {
      return null;
    }
    return pets.remove(index);
  }

  public Pet deletePetById(int id) {
    for (int i = 0; i < pets.size(); i++) {
      if (pets.get(i).getId() == id) {
        return pets.remove(i);
      }
    }
    return null;
  }

  public Pet updatePet(int index, Pet pet) {
    if (!isValidPetIndex(index) || pet == null) {
      return null;
    }
    pets.set(index, pet);
    return pet;
  }

  public Pet getPet(int index) {
    if (!isValidPetIndex(index)) {
      return null;
    }
    return pets.get(index);
  }

  public Pet getPet(String name) {
    for (Pet p : pets) {
      if (p.getName().equalsIgnoreCase(name)) {
        return p;
      }
    }
    return null;
  }

  public Pet getPetById(int id) {
    for (Pet p : pets) {
      if (p.getId() == id) {
        return p;
      }
    }
    return null;
  }

  public boolean isValidPetIndex(int index) {
    return index >= 0 && index < pets.size();
  }

  public int numberOfPets() {
    return pets.size();
  }

  public int numberOfDogs() {
    int count = 0;
    for (Pet p : pets) {
      if (p instanceof Dog) {
        count++;
      }
    }
    return count;
  }

  public int numberOfCats() {
    int count = 0;
    for (Pet p : pets) {
      if (p instanceof Cat) {
        count++;
      }
    }
    return count;
  }

  public int numberOfParrots() {
    int count = 0;
    for (Pet p : pets) {
      if (p instanceof Parrot) {
        count++;
      }
    }
    return count;
  }

  public int numberOfDangerousDogs() {
    int count = 0;
    for (Pet p : pets) {
      if (p instanceof Dog dog && dog.isDangerousBreed()) {
        count++;
      }
    }
    return count;
  }

  public int numberOfIndoorCats() {
    int count = 0;
    for (Pet p : pets) {
      if (p instanceof Cat cat && cat.isIndoorCat()) {
        count++;
      }
    }
    return count;
  }

  /**
   * Counts parrots whose vocab tier matches the tier {@code wordCount} maps to.
   *
   * <p>per spec, vocab is stored as a category String not a raw int, so we run
   * the input through {@link BirdUtility#vocabularyCategory(int)} first and
   * compare against each parrots stored tier.
   */
  public int numberOfParrotsByVocabularySize(int wordCount) {
    String target = BirdUtility.vocabularyCategory(wordCount);
    int count = 0;
    for (Pet p : pets) {
      if (p instanceof Parrot par && target.equalsIgnoreCase(par.getVocabularySize())) {
        count++;
      }
    }
    return count;
  }

  public String listAllPets() {
    return Utilities.joinIndexed(pets, p -> true, "No Pets");
  }

  public String listAllDogs() {
    return Utilities.joinIndexed(pets, p -> p instanceof Dog, "No Dogs");
  }

  public String listAllCats() {
    return Utilities.joinIndexed(pets, p -> p instanceof Cat, "No cats");
  }

  public String listAllParrots() {
    return Utilities.joinIndexed(pets, p -> p instanceof Parrot, "No Parrots");
  }

  public String listAllDangerousDogs() {
    return Utilities.joinIndexed(
        pets,
        p -> p instanceof Dog dog && dog.isDangerousBreed(),
        "No Dangerous Dogs in the Kennels");
  }

  public String listAllPetsByOwner(String ownerName) {
    return Utilities.joinIndexed(
        pets,
        p -> p.getOwner() != null && p.getOwner().getName().equalsIgnoreCase(ownerName),
        "No Pet with owner " + ownerName);
  }

  /**
   * Lists pets attending more than {@code days} days per week.
   *
   * <p>format is {@code "name (N days)"} per line, no leading index because the
   * report screen doesnt need one. joinIndexed doesnt fit here (different
   * shape), so we use an inline stream.
   */
  public String listAllPetsThatStayMoreThanDays(int days) {
    String result = pets.stream()
        .filter(p -> p.numOfDaysAttending() > days)
        .map(p -> p.getName() + " (" + p.numOfDaysAttending() + " days)")
        .collect(Collectors.joining("\n"));
    return result.isEmpty() ? "No Pet stays longer than " + days : result;
  }

  public String listOwners() {
    String result = pets.stream()
        .map(Pet::getOwner)
        .filter(Objects::nonNull)
        .map(Owner::getName)
        .distinct()
        .collect(Collectors.joining("\n"));
    return result.isEmpty() ? "(no owners)" : result;
  }

  public Pet findDogByOwnerAndBreedAndAge(String ownerName, String breed, int age) {
    for (Pet p : pets) {
      if (p instanceof Dog dog
          && dog.getOwner() != null
          && dog.getOwner().getName().equalsIgnoreCase(ownerName)
          && breed != null
          && breed.equalsIgnoreCase(dog.getBreed())
          && dog.getAge() == age) {
        return dog;
      }
    }
    return null;
  }

  public String getPetsByOwnersName(String ownerName) {
    String result = pets.stream()
        .filter(p -> p.getOwner() != null
            && p.getOwner().getName().equalsIgnoreCase(ownerName))
        .map(Pet::getName)
        .collect(Collectors.joining(", "));
    return result.isEmpty() ? "No Pets for " + ownerName : result;
  }

  /** Sorts {@code pets} by id, descending. Bubble sort, walks {@link #swapPets(int, int)}. */
  public void sortPetsById() {
    for (int pass = 0; pass < pets.size() - 1; pass++) {
      for (int j = 0; j < pets.size() - 1 - pass; j++) {
        if (pets.get(j).getId() < pets.get(j + 1).getId()) {
          swapPets(j, j + 1);
        }
      }
    }
  }

  /** Sorts {@code pets} by name (case insensitive), ascending. */
  public void sortPetsByName() {
    for (int pass = 0; pass < pets.size() - 1; pass++) {
      for (int j = 0; j < pets.size() - 1 - pass; j++) {
        String a = pets.get(j).getName();
        String b = pets.get(j + 1).getName();
        if (a.compareToIgnoreCase(b) > 0) {
          swapPets(j, j + 1);
        }
      }
    }
  }

  // both swap helpers are private per spec, sort routines above are the only
  // legitimate callers.
  private void swapPets(int i, int j) {
    if (!isValidPetIndex(i) || !isValidPetIndex(j)) {
      return;
    }
    Pet tmp = pets.get(i);
    pets.set(i, pets.get(j));
    pets.set(j, tmp);
  }

  public double getWeeklyIncome() {
    double total = 0;
    for (Pet p : pets) {
      total += p.calculateWeeklyFee();
    }
    return total;
  }

  public double getAverageNumDaysPerWeek() {
    if (pets.isEmpty()) {
      return 0;
    }
    int total = 0;
    for (Pet p : pets) {
      total += p.numOfDaysAttending();
    }
    return (double) total / pets.size();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (name != null) {
      this.name = Utilities.truncate(name, MAX_NAME_LENGTH);
    }
  }

  /** Same as {@link #setName(String)}, kept around for parity with the UML. */
  public void initName(String name) {
    if (name != null) {
      this.name = Utilities.truncate(name, MAX_NAME_LENGTH);
    }
  }

  public int getMaxNumberOfPets() {
    return maxNumberOfPets;
  }

  public void setMaxNumberOfPets(int maxNumberOfPets) {
    // negative cap would brick addPet silently since pets.size() >= -1 is
    // always true, so reject it. zero is allowed, means "full house".
    if (maxNumberOfPets >= 0) {
      this.maxNumberOfPets = maxNumberOfPets;
    }
  }

  public ArrayList<Pet> getPetsArray() {
    // defensive copy: returning the backing list would let callers add,
    // remove, or reorder pets without going through the capacity/null checks
    // in addPet(). still returns an ArrayList to match the UML signature.
    return new ArrayList<>(pets);
  }

  public void setPetsArray(ArrayList<Pet> pets) {
    if (pets != null) {
      // defensive copy in, same reasoning, and make sure we drop any incoming
      // null entries so the rest of the class doesnt have to guard for them.
      this.pets = new ArrayList<>(pets);
    }
  }

  @Override
  public String fileName() {
    return file.getName();
  }

  @Override
  public void save() throws Exception {
    XmlSerializer.saveToFile(pets, file);
  }

  @Override
  public void load() throws Exception {
    if (!file.exists()) {
      // first run, nothing to load. leave the empty list as is.
      return;
    }
    Object loaded = XmlSerializer.loadFromFile(file, ALLOWED_TYPES);
    if (!(loaded instanceof ArrayList<?> rawList)) {
      // fail loud on shape mismatch. silently keeping the empty list would let
      // the next save overwrite real data on disk with nothing, which is the
      // worst possible failure mode for a persistence layer.
      throw new IllegalStateException(
          "pets file root is not a List, got: "
              + (loaded == null ? "null" : loaded.getClass().getName()));
    }
    // walk the raw list so we can fail loud on mixed/wrong element types
    // instead of hiding it behind a blanket unchecked cast.
    ArrayList<Pet> typed = new ArrayList<>(rawList.size());
    for (Object item : rawList) {
      if (!(item instanceof Pet pet)) {
        throw new IllegalStateException(
            "pets file contains a non-Pet entry: "
                + (item == null ? "null" : item.getClass().getName()));
      }
      typed.add(pet);
    }
    this.pets = typed;
    // xstream bypasses the Pet ctor, so Pet.nextId didnt get bumped during
    // deserialisation. catch up now or a freshly added pet will collide with
    // a loaded one.
    Pet.recomputeNextId(this.pets);
  }

}
