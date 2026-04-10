package daycare.controllers;

import daycare.models.Bird;
import daycare.models.Cat;
import daycare.models.Dog;
import daycare.models.Mammal;
import daycare.models.Owner;
import daycare.models.Parrot;
import daycare.models.Pet;
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
 * assigns rolling integer ids to new pets, exposes count/list/sort/search
 * helpers used by the menu and reports screens, and persists the whole list
 * to XML via {@link XmlSerializer}.
 *
 * <p>note: most listing methods return a single newline delimited String so
 * the Driver can pipe them straight to {@code System.out.println}. zero
 * formatting smarts in here, the menu/Tui layer is in charge of styling.
 *
 * <p>id assignment: callers pass {@code id = 0} when adding a fresh pet,
 * the API rolls a new id from {@code nextPetId}. on load, {@code nextPetId}
 * is recomputed as {@code max(existing) + 1} so restored pets dont collide
 * with new ones.
 *
 * <p>Returns: instance, construct with name + max capacity + target file.
 *
 * <p>Example:
 * <pre>{@code
 * PetsDayCareAPI api = new PetsDayCareAPI("daycare", 100, new File("data/pets.xml"));
 * api.load();
 * Owner alice = ...;
 * Dog rex = new Dog("Rex", 3, alice, 0,
 *     'M', true, 25.5, true, "Labrador", false);
 * api.addPet(rex); // rex.getId() is now set
 * rex.checkIn(0);
 * rex.checkIn(1);
 * double income = api.getWeeklyIncome();
 * api.save();
 * }</pre>
 */
public class PetsDayCareAPI implements ISerializer {

  private String name;
  private int maxNumberOfPets;
  private File file;
  private ArrayList<Pet> pets;
  private int nextPetId;

  public PetsDayCareAPI(String name, int maxNumberOfPets, File file) {
    this.name = name;
    this.maxNumberOfPets = maxNumberOfPets;
    this.file = file;
    this.pets = new ArrayList<>();
    this.nextPetId = 1;
  }

  public boolean addPet(Pet pet) {
    if (pet == null) {
      return false;
    }
    if (pets.size() >= maxNumberOfPets) {
      return false;
    }
    if (pet.getId() == 0) {
      pet.setId(nextPetId++);
    } else if (pet.getId() >= nextPetId) {
      nextPetId = pet.getId() + 1;
    }
    return pets.add(pet);
  }

  public Pet removePet(int index) {
    if (!isValidPetIndex(index)) {
      return null;
    }
    return pets.remove(index);
  }

  public void updatePet(int index, Pet pet) {
    if (!isValidPetIndex(index)) {
      return;
    }
    pets.set(index, pet);
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

  public void swapPets(int i, int j) {
    if (!isValidPetIndex(i) || !isValidPetIndex(j)) {
      return;
    }
    Pet tmp = pets.get(i);
    pets.set(i, pets.get(j));
    pets.set(j, tmp);
  }

  public void swapPets(Pet a, Pet b) {
    int ai = pets.indexOf(a);
    int bi = pets.indexOf(b);
    if (ai >= 0 && bi >= 0) {
      swapPets(ai, bi);
    }
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

  public int numberOfParrotsByVocabularySize(int minSize) {
    int count = 0;
    for (Pet p : pets) {
      if (p instanceof Parrot par && par.getVocabularySize() >= minSize) {
        count++;
      }
    }
    return count;
  }

  public String listAllPets() {
    if (pets.isEmpty()) {
      return "(no pets)";
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

  public String listAllDangerousDogs() {
    String result = "";
    for (Pet p : pets) {
      if (p instanceof Dog dog && dog.isDangerousBreed()) {
        if (!result.isEmpty()) {
          result += "\n";
        }
        result += dog;
      }
    }
    return result.isEmpty() ? "(no dangerous dogs)" : result;
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
    return result.isEmpty() ? "(no pets for " + ownerName + ")" : result;
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
    return result.isEmpty() ? "(no pets stay more than " + days + " days)" : result;
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
    return result.isEmpty() ? "(none)" : result;
  }

  public void sortPetsById() {
    pets.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
  }

  public void sortPetsByName() {
    pets.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
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
    this.name = name;
  }

  /** Same as {@link #setName(String)}, kept around for parity with the UML. */
  public void initName(String name) {
    this.name = name;
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
        Owner.class, ArrayList.class, Boolean.class, Boolean[].class);
    if (loaded instanceof ArrayList<?> list) {
      this.pets = (ArrayList<Pet>) list;
    }
    recomputeNextPetId();
  }

  private void recomputeNextPetId() {
    int max = 0;
    for (Pet p : pets) {
      if (p.getId() > max) {
        max = p.getId();
      }
    }
    nextPetId = max + 1;
  }
}
