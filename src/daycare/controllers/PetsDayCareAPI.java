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
import daycare.utils.XmlSerializer;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

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

  private String name;
  private int maxNumberOfPets;
  private File file;
  private ArrayList<Pet> pets;

  public PetsDayCareAPI(String name, int maxNumberOfPets, File file) {
    this.name = truncate(name, MAX_NAME_LENGTH);
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
    if (pets.isEmpty()) {
      return "No Pets";
    }
    String result = "";
    for (int i = 0; i < pets.size(); i++) {
      if (!result.isEmpty()) {
        result += "\n";
      }
      result += i + ": " + pets.get(i);
    }
    return result;
  }

  public String listAllDogs() {
    String result = "";
    for (int i = 0; i < pets.size(); i++) {
      if (pets.get(i) instanceof Dog) {
        if (!result.isEmpty()) {
          result += "\n";
        }
        result += i + ": " + pets.get(i);
      }
    }
    return result.isEmpty() ? "No Dogs" : result;
  }

  public String listAllCats() {
    String result = "";
    for (int i = 0; i < pets.size(); i++) {
      if (pets.get(i) instanceof Cat) {
        if (!result.isEmpty()) {
          result += "\n";
        }
        result += i + ": " + pets.get(i);
      }
    }

    return result.isEmpty() ? "No cats" : result;
  }

  public String listAllParrots() {
    String result = "";
    for (int i = 0; i < pets.size(); i++) {
      if (pets.get(i) instanceof Parrot) {
        if (!result.isEmpty()) {
          result += "\n";
        }
        result += i + ": " + pets.get(i);
      }
    }
    return result.isEmpty() ? "No Parrots" : result;
  }

  public String listAllDangerousDogs() {
    String result = "";
    for (int i = 0; i < pets.size(); i++) {
      if (pets.get(i) instanceof Dog dog && dog.isDangerousBreed()) {
        if (!result.isEmpty()) {
          result += "\n";
        }
        result += i + ": " + dog;
      }
    }
    return result.isEmpty() ? "No Dangerous Dogs in the Kennels" : result;
  }

  public String listAllPetsByOwner(String ownerName) {
    String result = "";
    for (int i = 0; i < pets.size(); i++) {
      Pet p = pets.get(i);
      if (p.getOwner() != null && p.getOwner().getName().equalsIgnoreCase(ownerName)) {
        if (!result.isEmpty()) {
          result += "\n";
        }
        result += i + ": " + p;
      }
    }
    return result.isEmpty() ? "No Pet with owner " + ownerName : result;
  }

  public String listAllPetsThatStayMoreThanDays(int days) {
    String result = "";
    for (Pet p : pets) {
      if (p.numOfDaysAttending() > days) {
        if (!result.isEmpty()) {
          result += "\n";
        }
        result += p.getName() + " (" + p.numOfDaysAttending() + " days)";
      }
    }
    return result.isEmpty() ? "No Pet stays longer than " + days : result;
  }

  public String listOwners() {
    Set<String> seen = new LinkedHashSet<>();
    for (Pet p : pets) {
      if (p.getOwner() != null) {
        seen.add(p.getOwner().getName());
      }
    }
    if (seen.isEmpty()) {
      return "(no owners)";
    }
    String result = "";
    for (String n : seen) {
      if (!result.isEmpty()) {
        result += "\n";
      }
      result += n;
    }
    return result;
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
    String result = "";
    for (Pet p : pets) {
      if (p.getOwner() != null && p.getOwner().getName().equalsIgnoreCase(ownerName)) {
        if (!result.isEmpty()) {
          result += ", ";
        }
        result += p.getName();
      }
    }
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

  private void swapPets(Pet a, Pet b) {
    int ai = pets.indexOf(a);
    int bi = pets.indexOf(b);
    if (ai >= 0 && bi >= 0) {
      swapPets(ai, bi);
    }
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
      this.name = truncate(name, MAX_NAME_LENGTH);
    }
  }

  /** Same as {@link #setName(String)}, kept around for parity with the UML. */
  public void initName(String name) {
    if (name != null) {
      this.name = truncate(name, MAX_NAME_LENGTH);
    }
  }

  public int getMaxNumberOfPets() {
    return maxNumberOfPets;
  }

  public void setMaxNumberOfPets(int maxNumberOfPets) {
    this.maxNumberOfPets = maxNumberOfPets;
  }

  public ArrayList<Pet> getPetsArray() {
    return pets;
  }

  public void setPetsArray(ArrayList<Pet> pets) {
    this.pets = pets;
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
  @SuppressWarnings("unchecked")
  public void load() throws Exception {
    if (!file.exists()) {
      // first run, nothing to load. leave the empty list as is.
      return;
    }
    Object loaded = XmlSerializer.loadFromFile(file,
        Pet.class, Mammal.class, Bird.class,
        Dog.class, Cat.class, Parrot.class,
        Owner.class, ArrayList.class, boolean[].class);
    if (loaded instanceof ArrayList<?> list) {
      this.pets = (ArrayList<Pet>) list;
    }
  }

  private static String truncate(String value, int max) {
    if (value == null) {
      return null;
    }
    return value.length() <= max ? value : value.substring(0, max);
  }
}
