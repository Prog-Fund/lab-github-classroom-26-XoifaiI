package daycare.main;

import daycare.controllers.OwnerAPI;
import daycare.controllers.PetsDayCareAPI;
import daycare.models.Cat;
import daycare.models.Dog;
import daycare.models.Owner;
import daycare.models.Parrot;
import daycare.models.Pet;
import daycare.tui.MenuItem;
import daycare.tui.Tui;
import daycare.utils.BirdUtility;
import daycare.utils.CatToyUtility;
import daycare.utils.DogBreedUtility;
import daycare.utils.ScannerInput;
import daycare.utils.Utilities;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Driver: app entry point that wires Menu + ScannerInput + controllers together.
 *
 * <p>stdin and stdout live here only. the controllers, models, and tui layer
 * never touch either one, they just compute and return strings.
 *
 * <p>state: one OwnerAPI, one PetsDayCareAPI, one shared Scanner. the
 * {@code status} string is what the next menu redraw shows under the box.
 *
 * <p>persistence: {@link #tryLoad()} and {@link #trySave()} wrap the API
 * calls in try/catch and stuff any error into {@code status}. requires the
 * xstream jar at runtime.
 */
public final class Driver {

  private static final File DATA_DIR = new File("data");
  private static final File OWNERS_FILE = new File(DATA_DIR, "owners.xml");
  private static final File PETS_FILE = new File(DATA_DIR, "pets.xml");
  private static final int MAX_PETS = 100;
  private static final int MENU_WIDTH = 60;

  private static final Scanner SCANNER = new Scanner(System.in);
  private static final ScannerInput INPUT = new ScannerInput(SCANNER);
  private static final OwnerAPI OWNERS = new OwnerAPI("daycare-owners", OWNERS_FILE);
  private static final PetsDayCareAPI PETS =
      new PetsDayCareAPI("daycare", MAX_PETS, PETS_FILE);

  private static String status = "(no actions yet)";

  private Driver() {}

  public static void main(String[] args) {
    Tui.enableColor();
    clearScreen();
    tryLoad();

    runMainMenuLoop();

    System.out.println();
    System.out.println("  bye!");
  }

  private static void runMainMenuLoop() {
    while (true) {
      clearScreen();
      String choice = showMenu(mainMenu());

      switch (choice) {
        case "1" -> petsMenuLoop();
        case "2" -> ownersMenuLoop();
        case "3" -> reportsMenuLoop();
        case "4" -> tryLoad();
        case "5" -> trySave();
        case "0" -> { return; }
        default -> status = unknownOption(choice);
      }
    }
  }

  private static void petsMenuLoop() {
    while (true) {
      clearScreen();
      String choice = showMenu(petsMenu());

      switch (choice) {
        case "1" -> addDog();
        case "2" -> addCat();
        case "3" -> addParrot();
        case "4" -> show("All Pets", PETS.listAllPets());
        case "5" -> removePet();
        case "6" -> checkInPet();
        case "7" -> checkOutPet();
        case "8" -> { PETS.sortPetsByName(); status = "sorted by name"; }
        case "9" -> { PETS.sortPetsById(); status = "sorted by id"; }
        case "0" -> { return; }
        default -> status = unknownOption(choice);
      }
    }
  }

  private static void ownersMenuLoop() {
    while (true) {
      clearScreen();
      String choice = showMenu(ownersMenu());

      switch (choice) {
        case "1" -> addOwner();
        case "2" -> show("All Owners", OWNERS.listAllOwners());
        case "3" -> removeOwner();
        case "0" -> { return; }
        default -> status = unknownOption(choice);
      }
    }
  }

  private static void reportsMenuLoop() {
    while (true) {
      clearScreen();
      String choice = showMenu(reportsMenu());

      switch (choice) {
        case "1" -> show("Weekly Income", Utilities.formatMoney(PETS.getWeeklyIncome()));
        case "2" -> show("Average Days / Pet",
            String.format("%.2f", PETS.getAverageNumDaysPerWeek()));
        case "3" -> show("Pet Counts", petCountsReport());
        case "4" -> show("Dangerous Dogs", PETS.listAllDangerousDogs());
        case "5" -> petsByOwnerReport();
        case "6" -> petsStayingManyDaysReport();
        case "0" -> { return; }
        default -> status = unknownOption(choice);
      }
    }
  }

  private static Menu mainMenu() {
    return new Menu("Doggie Day Care")
        .subtitle("management console")
        .breadcrumb("Home")
        .width(MENU_WIDTH)
        .showBanner(true)
        .section("Manage", List.of(
            new MenuItem(1, "Pets"),
            new MenuItem(2, "Owners"),
            new MenuItem(3, "Reports")))
        .section("Data", List.of(
            new MenuItem(4, "Load All"),
            new MenuItem(5, "Save All")))
        .item(new MenuItem(0, "Exit"))
        .status(status)
        .hint("[0] to exit");
  }

  private static Menu petsMenu() {
    return new Menu("Pets")
        .subtitle(Utilities.pluralize(PETS.numberOfPets(), "pet", "pets") + " on file")
        .breadcrumb("Home > Pets")
        .width(MENU_WIDTH)
        .section("Add", List.of(
            new MenuItem(1, "Add Dog"),
            new MenuItem(2, "Add Cat"),
            new MenuItem(3, "Add Parrot")))
        .section("Manage", List.of(
            new MenuItem(4, "List All Pets"),
            new MenuItem(5, "Remove Pet"),
            new MenuItem(6, "Check In Pet"),
            new MenuItem(7, "Check Out Pet"),
            new MenuItem(8, "Sort by Name"),
            new MenuItem(9, "Sort by Id")))
        .item(new MenuItem(0, "Back"))
        .status(status)
        .hint("[0] back to main menu");
  }

  private static Menu ownersMenu() {
    return new Menu("Owners")
        .subtitle(Utilities.pluralize(OWNERS.numberOfOwners(), "owner", "owners") + " on file")
        .breadcrumb("Home > Owners")
        .width(MENU_WIDTH)
        .section("Manage", List.of(
            new MenuItem(1, "Add Owner"),
            new MenuItem(2, "List All Owners"),
            new MenuItem(3, "Remove Owner")))
        .item(new MenuItem(0, "Back"))
        .status(status)
        .hint("[0] back to main menu");
  }

  private static Menu reportsMenu() {
    return new Menu("Reports")
        .breadcrumb("Home > Reports")
        .width(MENU_WIDTH)
        .section("Money", List.of(
            new MenuItem(1, "Weekly Income"),
            new MenuItem(2, "Average Days / Pet")))
        .section("Counts", List.of(
            new MenuItem(3, "Pet Counts by Type"),
            new MenuItem(4, "List Dangerous Dogs"),
            new MenuItem(5, "Pets by Owner"),
            new MenuItem(6, "Pets Staying Many Days")))
        .item(new MenuItem(0, "Back"))
        .status(status)
        .hint("[0] back to main menu");
  }

  private static void addDog() {
    Owner owner = pickOwner();
    if (owner == null) {
      return;
    }

    String name = promptString("  name: ");
    int age = promptInt("  age: ");
    char sex = promptChar("  sex (M/F/U): ");
    boolean vaccinated = promptBoolean("  vaccinated? (y/n): ");
    double weight = promptDouble("  weight (kg): ");
    boolean neutered = promptBoolean("  neutered? (y/n): ");

    System.out.println("  known breeds: " + DogBreedUtility.getAllBreeds());
    String breed = promptString("  breed: ");
    boolean dangerous = DogBreedUtility.isDangerous(breed);

    Dog dog = new Dog(name, age, owner, 0,
        sex, vaccinated, weight, neutered, breed, dangerous);

    if (PETS.addPet(dog)) {
      status = "added dog " + dog.getName() + " (id " + dog.getId() + ")"
          + (dangerous ? " [dangerous breed flag set automatically]" : "");
    } else {
      status = "couldnt add dog (at capacity?)";
    }
  }

  private static void addCat() {
    Owner owner = pickOwner();
    if (owner == null) {
      return;
    }

    String name = promptString("  name: ");
    int age = promptInt("  age: ");
    char sex = promptChar("  sex (M/F/U): ");
    boolean vaccinated = promptBoolean("  vaccinated? (y/n): ");
    double weight = promptDouble("  weight (kg): ");
    boolean neutered = promptBoolean("  neutered? (y/n): ");
    boolean indoor = promptBoolean("  indoor cat? (y/n): ");

    System.out.println("  toy suggestions: " + CatToyUtility.getAllToys());
    String toy = promptString("  favourite toy: ");

    Cat cat = new Cat(name, age, owner, 0,
        sex, vaccinated, weight, neutered, indoor, toy);

    if (PETS.addPet(cat)) {
      status = "added cat " + cat.getName() + " (id " + cat.getId() + ")";
    } else {
      status = "couldnt add cat (at capacity?)";
    }
  }

  private static void addParrot() {
    Owner owner = pickOwner();
    if (owner == null) {
      return;
    }

    String name = promptString("  name: ");
    int age = promptInt("  age: ");

    System.out.println("  known species: " + BirdUtility.getAllSpecies());
    // species is suggestion only, Parrot doesnt store it (no field per spec).
    promptString("  species (free text, not stored): ");

    double wingSpan = promptDouble("  wing span (cm): ");
    boolean canFly = promptBoolean("  can fly? (y/n): ");
    int vocab = promptInt("  vocabulary size (words): ");

    Parrot parrot = new Parrot(name, age, owner, 0, wingSpan, canFly, vocab);

    if (PETS.addPet(parrot)) {
      status = "added parrot " + parrot.getName() + " (id " + parrot.getId()
          + ", vocab tier " + parrot.getVocabularySize() + ")";
    } else {
      status = "couldnt add parrot (at capacity?)";
    }
  }

  private static void removePet() {
    if (PETS.numberOfPets() == 0) {
      status = "nothing to remove";
      return;
    }

    show("All Pets", PETS.listAllPets());
    int idx = promptInt("  pet index to remove: ");

    Pet removed = PETS.deletePetByIndex(idx);
    status = removed != null
        ? "removed " + removed.getName()
        : "no pet at index " + idx;
  }

  private static void checkInPet() {
    Pet pet = selectPet("no pets to check in");
    if (pet == null) {
      return;
    }

    int day = promptInt("  day (0=Mon ... 5=Sat): ");
    if (!Utilities.validRange(day, 0, Pet.DAYS_PER_WEEK - 1)) {
      status = "invalid day: " + day;
      return;
    }

    pet.checkIn(day);
    status = pet.getName() + " checked in for " + Utilities.dayName(day);
  }

  private static void checkOutPet() {
    Pet pet = selectPet("no pets to check out");
    if (pet == null) {
      return;
    }

    int day = promptInt("  day (0=Mon ... 5=Sat): ");
    if (!Utilities.validRange(day, 0, Pet.DAYS_PER_WEEK - 1)) {
      status = "invalid day: " + day;
      return;
    }

    pet.checkOut(day);
    status = pet.getName() + " checked out for " + Utilities.dayName(day);
  }

  /** shared shell for check in / check out: list, prompt idx, resolve pet. null means bail. */
  private static Pet selectPet(String emptyStatus) {
    if (PETS.numberOfPets() == 0) {
      status = emptyStatus;
      return null;
    }

    show("All Pets", PETS.listAllPets());
    int idx = promptInt("  pet index: ");

    Pet pet = PETS.getPet(idx);
    if (pet == null) {
      status = "no pet at index " + idx;
    }
    return pet;
  }

  private static void addOwner() {
    String name = promptString("  name: ");
    String address = promptString("  address: ");
    String phone = promptString("  phone: ");
    String email = promptString("  email: ");

    Owner owner = OWNERS.addOwner(name, address, phone, email);
    status = "added owner " + owner.getName() + " (id " + owner.getId() + ")";
  }

  private static void removeOwner() {
    if (OWNERS.numberOfOwners() == 0) {
      status = "nothing to remove";
      return;
    }

    show("All Owners", OWNERS.listAllOwners());
    int idx = promptInt("  owner index to remove: ");

    Owner removed = OWNERS.removeOwner(idx);
    status = removed != null
        ? "removed " + removed.getName()
        : "no owner at index " + idx;
  }

  private static Owner pickOwner() {
    if (OWNERS.numberOfOwners() == 0) {
      status = "add an owner first before adding pets";
      return null;
    }

    show("Owners", OWNERS.listAllOwners());
    int idx = promptInt("  owner index: ");

    Owner owner = OWNERS.getOwner(idx);
    if (owner == null) {
      status = "no owner at index " + idx;
    }
    return owner;
  }

  private static String petCountsReport() {
    return "dogs: " + PETS.numberOfDogs() + "\n"
        + "  dangerous: " + PETS.numberOfDangerousDogs() + "\n"
        + "cats: " + PETS.numberOfCats() + "\n"
        + "  indoor: " + PETS.numberOfIndoorCats() + "\n"
        + "parrots: " + PETS.numberOfParrots();
  }

  private static void petsByOwnerReport() {
    String owner = promptString("  owner name: ");
    show("Pets owned by " + owner, PETS.listAllPetsByOwner(owner));
  }

  private static void petsStayingManyDaysReport() {
    int days = promptInt("  more than how many days: ");
    show("Pets staying > " + days + " days",
        PETS.listAllPetsThatStayMoreThanDays(days));
  }

  private static void show(String title, String body) {
    clearScreen();
    System.out.println();
    System.out.println("  " + Tui.bold(title));
    System.out.println();
    // body gets sanitised on the way out, list builders concat Pet / Owner
    // toString which includes user controlled fields. a loaded xml could
    // carry a terminal escape even if the input boundary was clean.
    System.out.println(Tui.sanitize(body));
    System.out.println();
    pause();
  }

  private static void tryLoad() {
    try {
      OWNERS.load();
      PETS.load();
      status = "loaded "
          + Utilities.pluralize(PETS.numberOfPets(), "pet", "pets")
          + " and "
          + Utilities.pluralize(OWNERS.numberOfOwners(), "owner", "owners");
    } catch (Exception e) {
      status = "load failed: " + e.getMessage();
    }
  }

  private static void trySave() {
    try {
      if (!DATA_DIR.exists()) {
        DATA_DIR.mkdirs();
      }
      OWNERS.save();
      PETS.save();
      status = "saved everything to " + DATA_DIR.getPath();
    } catch (Exception e) {
      status = "save failed: " + e.getMessage();
    }
  }

  private static String unknownOption(String choice) {
    return "unknown option: " + (choice.isEmpty() ? "(empty)" : choice);
  }

  private static String showMenu(Menu menu) {
    for (String line : menu.lines()) {
      System.out.println(line);
    }
    System.out.print("  " + Tui.accent(">") + " ");
    return INPUT.readLine().orElse("");
  }

  private static void clearScreen() {
    System.out.print(Tui.clearSequence());
    System.out.flush();
  }

  private static void pause() {
    System.out.print(Tui.pausePrompt());
    INPUT.readLine();
  }

  private static String promptString(String label) {
    System.out.print(label);
    // scrub control chars at the input boundary so a pasted ESC[2J never
    // reaches the data model. output side still scrubs in show() for the
    // loaded xml case.
    return Tui.sanitize(INPUT.readLine().orElse(""));
  }

  private static int promptInt(String label) {
    Optional<Integer> result;
    do {
      System.out.print(label);
      result = INPUT.readInt();
      if (result.isEmpty()) {
        System.out.println("  not a whole number, try again");
      }
    } while (result.isEmpty());
    return result.get();
  }

  private static double promptDouble(String label) {
    Optional<Double> result;
    do {
      System.out.print(label);
      result = INPUT.readDouble();
      if (result.isEmpty()) {
        System.out.println("  not a number, try again");
      }
    } while (result.isEmpty());
    return result.get();
  }

  private static boolean promptBoolean(String label) {
    Optional<Boolean> result;
    do {
      System.out.print(label);
      result = INPUT.readBoolean();
      if (result.isEmpty()) {
        System.out.println("  please answer y or n");
      }
    } while (result.isEmpty());
    return result.get();
  }

  private static char promptChar(String label) {
    Optional<Character> result;
    do {
      System.out.print(label);
      result = INPUT.readChar();
      if (result.isEmpty()) {
        System.out.println("  please type at least one character");
      }
    } while (result.isEmpty());
    return result.get();
  }
}
