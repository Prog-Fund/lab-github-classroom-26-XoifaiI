package daycare.utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * DogBreedUtility: known breeds plus the dangerous flag, keyed for O(1) lookup.
 *
 * <p>{@link #canonicalBreed(String)} returns the title cased form stored in
 * the table so callers can normalise user input before storing it.
 */
public final class DogBreedUtility {

  private record BreedInfo(String canonical, boolean dangerous) {}

  // keys MUST be the canonical name lowercased, otherwise isKnownBreed returns
  // false for the canonical form.
  private static final Map<String, BreedInfo> BREEDS = Map.of(
      "labrador retriever", new BreedInfo("Labrador Retriever", false),
      "german shepherd", new BreedInfo("German Shepherd", false),
      "golden retriever", new BreedInfo("Golden Retriever", false),
      "bulldog", new BreedInfo("Bulldog", false),
      "beagle", new BreedInfo("Beagle", false),
      "rottweiler", new BreedInfo("Rottweiler", true),
      "pit bull", new BreedInfo("Pit Bull", true));

  private DogBreedUtility() {}

  public static boolean isDangerous(String breed) {
    BreedInfo info = lookup(breed);
    return info != null && info.dangerous();
  }

  public static boolean isKnownBreed(String breed) {
    return lookup(breed) != null;
  }

  /** title cased form of {@code breed}, or null if its not in the table. */
  public static String canonicalBreed(String breed) {
    BreedInfo info = lookup(breed);
    return info == null ? null : info.canonical();
  }

  public static List<String> getAllBreeds() {
    return BREEDS.values().stream().map(BreedInfo::canonical).toList();
  }

  public static List<String> getDangerousBreeds() {
    return BREEDS.values().stream()
        .filter(BreedInfo::dangerous)
        .map(BreedInfo::canonical)
        .toList();
  }

  public static List<String> getNonDangerousBreeds() {
    return BREEDS.values().stream()
        .filter(b -> !b.dangerous())
        .map(BreedInfo::canonical)
        .toList();
  }

  // Locale.ROOT so a JVM started with -Duser.language=tr doesnt mangle 'I'
  // and miss matches.
  private static BreedInfo lookup(String breed) {
    if (breed == null) {
      return null;
    }
    return BREEDS.get(breed.toLowerCase(Locale.ROOT));
  }
}
