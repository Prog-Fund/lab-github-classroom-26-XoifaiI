package daycare.utils;

import java.util.List;
import java.util.Map;

/**
 * DogBreedUtility: static lookup table of known dog breeds + which ones are flagged dangerous.
 *
 * <p>used by the menu/forms layer when prompting the user to pick a breed, and
 * by PetsDayCareAPI when it wants to auto populate the dangerousBreed flag on
 * a Dog. the list is from memory, tweak the constants if u want a different
 * roster.
 *
 * <p>Returns: static helpers only, dont instantiate.
 *
 * <p>Example:
 * <pre>{@code
 * boolean scary = DogBreedUtility.isDangerous("Rottweiler"); // true
 * List<String> all = DogBreedUtility.getAllBreeds();
 * List<String> safe = DogBreedUtility.getNonDangerousBreeds();
 * }</pre>
 */
public final class DogBreedUtility {

  /** breed name -> true if its on the dangerous list. */
  private static final Map<String, Boolean> BREEDS = Map.ofEntries(
      Map.entry("Labrador", false),
      Map.entry("Golden Retriever", false),
      Map.entry("Beagle", false),
      Map.entry("Poodle", false),
      Map.entry("Bulldog", false),
      Map.entry("German Shepherd", false),
      Map.entry("Border Collie", false),
      Map.entry("Pit Bull Terrier", true),
      Map.entry("Rottweiler", true),
      Map.entry("Doberman", true),
      Map.entry("Akita", true),
      Map.entry("Tosa", true),
      Map.entry("Fila Brasileiro", true));

  private DogBreedUtility() {}

  /** Returns true if {@code breed} is on the dangerous list. unknown breeds default to false. */
  public static boolean isDangerous(String breed) {
    return BREEDS.getOrDefault(breed, Boolean.FALSE);
  }

  /** Returns true if the breed string is recognised at all. */
  public static boolean isKnownBreed(String breed) {
    return BREEDS.containsKey(breed);
  }

  /** Every breed name we know about, in no particular order. */
  public static List<String> getAllBreeds() {
    return List.copyOf(BREEDS.keySet());
  }

  /** Just the breeds flagged dangerous. */
  public static List<String> getDangerousBreeds() {
    return BREEDS.entrySet().stream()
        .filter(Map.Entry::getValue)
        .map(Map.Entry::getKey)
        .toList();
  }

  /** Just the breeds flagged safe. */
  public static List<String> getNonDangerousBreeds() {
    return BREEDS.entrySet().stream()
        .filter(e -> !e.getValue())
        .map(Map.Entry::getKey)
        .toList();
  }
}
