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
 * <p>sorting should not use library sort (spec rule). hand rolled binary
 * insertion sort calls the private {@link #swapPets(int, int)}. sortPetsById
 * is descending, sortPetsByName is ascending.
 */
public class PetsDayCareAPI implements ISerializer {

  public static final int MAX_NAME_LENGTH = 20;

  /**
   * xstream allowlist for {@link #load()}. should include every concrete
   * class that can appear in the saved pet graph including the ArrayList
   * wrapper and the primitive boolean[] used for Pet.daysAttending. nothing
   * risky (Runtime, ProcessBuilder, JdbcRowSetImpl) should ever go here,
   * thats exactly the gadget chain surface allowTypes is meant to block.
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
    if (pet == null || pets.size() >= maxNumberOfPets) {
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
    return Utilities.countWhere(pets, p -> p instanceof Dog);
  }

  public int numberOfCats() {
    return Utilities.countWhere(pets, p -> p instanceof Cat);
  }

  public int numberOfParrots() {
    return Utilities.countWhere(pets, p -> p instanceof Parrot);
  }

  public int numberOfDangerousDogs() {
    return Utilities.countWhere(pets, p -> p instanceof Dog dog && dog.isDangerousBreed());
  }

  public int numberOfIndoorCats() {
    return Utilities.countWhere(pets, p -> p instanceof Cat cat && cat.isIndoorCat());
  }

  /** counts parrots whose vocab tier matches the tier {@code wordCount} maps to. */
  public int numberOfParrotsByVocabularySize(int wordCount) {
    String target = BirdUtility.vocabularyCategory(wordCount);
    return Utilities.countWhere(
        pets,
        p -> p instanceof Parrot par && target.equalsIgnoreCase(par.getVocabularySize()));
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

  /** format is {@code "name (N days)"} per line, no leading index. */
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

  /** binary insertion sort by id, descending. */
  public void sortPetsById() {
    for (int i = 1; i < pets.size(); i++) {
      int insertionIndex = binarySearchDescendingById(0, i - 1, pets.get(i).getId());
      for (int j = i; j > insertionIndex; j--) {
        swapPets(j, j - 1);
      }
    }
  }

  private int binarySearchDescendingById(int low, int high, int targetId) {
    while (low <= high) {
      int mid = low + (high - low) / 2;
      if (pets.get(mid).getId() < targetId) {
        high = mid - 1;
      } else {
        low = mid + 1;
      }
    }
    return low;
  }

  /** binary insertion sort by name (case insensitive), ascending. */
  public void sortPetsByName() {
    for (int i = 1; i < pets.size(); i++) {
      int insertionIndex = binarySearchAscendingByName(0, i - 1, pets.get(i).getName());
      for (int j = i; j > insertionIndex; j--) {
        swapPets(pets.get(j), pets.get(j - 1));
      }
    }
  }

  private int binarySearchAscendingByName(int low, int high, String targetName) {
    while (low <= high) {
      int mid = low + (high - low) / 2;
      if (pets.get(mid).getName().compareToIgnoreCase(targetName) > 0) {
        high = mid - 1;
      } else {
        low = mid + 1;
      }
    }
    return low;
  }

  // private per spec, sort routines above should be the only callers.
  private void swapPets(int i, int j) {
    if (!isValidPetIndex(i) || !isValidPetIndex(j)) {
      return;
    }
    Pet tmp = pets.get(i);
    pets.set(i, pets.get(j));
    pets.set(j, tmp);
  }

  // reference equality, not indexOf, because Dog.equals matches on breed only
  // and would collapse distinct-but-equal dogs to the first match.
  private void swapPets(Pet i, Pet j) {
    int a = -1;
    int b = -1;
    for (int k = 0; k < pets.size(); k++) {
      if (pets.get(k) == i) a = k;
      if (pets.get(k) == j) b = k;
    }
    swapPets(a, b);
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

  /** same as {@link #setName(String)}, kept around for parity with the UML. */
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
    // always true. zero is allowed, means full house.
    if (maxNumberOfPets >= 0) {
      this.maxNumberOfPets = maxNumberOfPets;
    }
  }

  public ArrayList<Pet> getPetsArray() {
    // defensive copy, returning the backing list would let callers reorder
    // pets without going through addPet.
    return new ArrayList<>(pets);
  }

  public void setPetsArray(ArrayList<Pet> pets) {
    if (pets != null) {
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
      return;
    }
    Object loaded = XmlSerializer.loadFromFile(file, ALLOWED_TYPES);
    if (!(loaded instanceof ArrayList<?> rawList)) {
      // fail loud on shape mismatch, the next save would otherwise overwrite
      // real data on disk with an empty list.
      throw new IllegalStateException(
          "pets file root is not a List, got: "
              + (loaded == null ? "null" : loaded.getClass().getName()));
    }
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
    Pet.recomputeNextId(this.pets);
  }
}
