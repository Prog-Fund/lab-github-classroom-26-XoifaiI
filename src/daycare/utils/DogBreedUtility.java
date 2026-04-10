package daycare.utils;

import java.util.List;
import java.util.Map;

/**
 * DogBreedUtility: static lookup table of known dog breeds + which ones are flagged dangerous.
 *
 * <p>used by the menu/forms layer when prompting the user to pick a breed, and
 * by Dog when it wants to validate the breed string in its ctor/setter. lookup
 * is case insensitive (the spec says so) but the canonical form returned by
 * {@link #getAllBreeds()} is the title cased version stored in the table.
 *
 * <p>Returns: static helpers only, dont instantiate.
 *
 * <p>Example:
 * <pre>{@code
 * boolean ok = DogBreedUtility.isKnownBreed("rottweiler"); // true
 * boolean scary = DogBreedUtility.isDangerous("Pit Bull"); // true
 * List<String> all = DogBreedUtility.getAllBreeds();
 * }</pre>
 */
public final class DogBreedUtility {

  /** breed name -> true if its on the dangerous list. spec defined roster. */
  private static final Map<String, Boolean> BREEDS = Map.of(
      "Labrador Retriever", false,
      "German Shepherd", false,
      "Golden Retriever", false,
      "Bulldog", false,
      "Beagle", false,
      "Rottweiler", true,
      "Pit Bull", true);

  private DogBreedUtility() {}

  /** Returns true if {@code breed} is on the dangerous list. lookup is case insensitive. */
  public static boolean isDangerous(String breed) {
    if (breed == null) {
      return false;
    }
    for (Map.Entry<String, Boolean> entry : BREEDS.entrySet()) {
      if (entry.getKey().equalsIgnoreCase(breed)) {
        return entry.getValue();
      }
    }
    return false;
  }

  /** Returns true if the breed string matches one of the known breeds (case insensitive). */
  public static boolean isKnownBreed(String breed) {
    if (breed == null) {
      return false;
    }
    for (String known : BREEDS.keySet()) {
      if (known.equalsIgnoreCase(breed)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the canonical (title-cased) form of {@code breed}, or null if its
   * not in the table. handy for normalising user input before storing it.
   */
  public static String canonicalBreed(String breed) {
    if (breed == null) {
      return null;
    }
    for (String known : BREEDS.keySet()) {
      if (known.equalsIgnoreCase(breed)) {
        return known;
      }
    }
    return null;
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
