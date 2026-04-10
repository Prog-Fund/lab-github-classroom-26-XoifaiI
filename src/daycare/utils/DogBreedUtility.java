package daycare.utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * DogBreedUtility: static lookup table of known dog breeds + which ones are flagged dangerous.
 *
 * <p>used by the menu/forms layer when prompting the user to pick a breed, and
 * by Dog when it wants to validate the breed string in its ctor/setter. lookup
 * is case insensitive (the spec says so) but the canonical form returned by
 * {@link #canonicalBreed(String)} is the title cased version stored in the table.
 *
 * <p>internally the table is keyed on the lowercased breed name so all three
 * single-breed lookups ({@link #isDangerous}, {@link #isKnownBreed},
 * {@link #canonicalBreed}) are O(1) expected, no per-call scans like the old
 * version. the value is a {@link BreedInfo} holding both the canonical casing
 * and the dangerous flag, so one Map hit gives us everything a caller needs.
 *
 * <p>Returns: static helpers only, dont instantiate.
 *
 * <p>Example:
 * <pre>{@code
 * boolean ok = DogBreedUtility.isKnownBreed("rottweiler"); // true
 * boolean scary = DogBreedUtility.isDangerous("Pit Bull"); // true
 * String canon = DogBreedUtility.canonicalBreed("beagle"); // "Beagle"
 * List<String> all = DogBreedUtility.getAllBreeds();
 * }</pre>
 */
public final class DogBreedUtility {

  /**
   * Pair stored in the lookup map. {@code canonical} is the title cased form
   * we hand back to callers so names render nicely on screen; {@code dangerous}
   * is the flag from the spec defined roster.
   */
  private record BreedInfo(String canonical, boolean dangerous) {}

  /**
   * lowercase breed name -> info. Map.of is immutable and the keys are
   * pre-lowercased at source so lookups are a single HashMap.get after
   * {@code input.toLowerCase(Locale.ROOT)}, no iteration.
   *
   * <p>when adding a breed here: the key MUST be the value's canonical name
   * lowercased, otherwise isKnownBreed returns false for the canonical form.
   * 7 entries is small enough that eyeballing the invariant is fine.
   */
  private static final Map<String, BreedInfo> BREEDS = Map.of(
      "labrador retriever", new BreedInfo("Labrador Retriever", false),
      "german shepherd", new BreedInfo("German Shepherd", false),
      "golden retriever", new BreedInfo("Golden Retriever", false),
      "bulldog", new BreedInfo("Bulldog", false),
      "beagle", new BreedInfo("Beagle", false),
      "rottweiler", new BreedInfo("Rottweiler", true),
      "pit bull", new BreedInfo("Pit Bull", true));

  private DogBreedUtility() {}

  /** Returns true if {@code breed} is on the dangerous list. lookup is case insensitive. */
  public static boolean isDangerous(String breed) {
    BreedInfo info = lookup(breed);
    return info != null && info.dangerous();
  }

  /** Returns true if the breed string matches one of the known breeds (case insensitive). */
  public static boolean isKnownBreed(String breed) {
    return lookup(breed) != null;
  }

  /**
   * Returns the canonical (title cased) form of {@code breed}, or null if its
   * not in the table. handy for normalising user input before storing it.
   */
  public static String canonicalBreed(String breed) {
    BreedInfo info = lookup(breed);
    return info == null ? null : info.canonical();
  }

  /** Every breed name we know about, in no particular order. */
  public static List<String> getAllBreeds() {
    return BREEDS.values().stream().map(BreedInfo::canonical).toList();
  }

  /** Just the breeds flagged dangerous. */
  public static List<String> getDangerousBreeds() {
    return BREEDS.values().stream()
        .filter(BreedInfo::dangerous)
        .map(BreedInfo::canonical)
        .toList();
  }

  /** Just the breeds flagged safe. */
  public static List<String> getNonDangerousBreeds() {
    return BREEDS.values().stream()
        .filter(b -> !b.dangerous())
        .map(BreedInfo::canonical)
        .toList();
  }

  /**
   * Single lookup path used by all three public single-breed methods.
   *
   * <p>null safe on the input, O(1) on the hit, Locale.ROOT so a JVM started
   * with -Duser.language=tr doesnt mangle "I" -> "\u0131" and miss matches.
   */
  private static BreedInfo lookup(String breed) {
    if (breed == null) {
      return null;
    }
    return BREEDS.get(breed.toLowerCase(Locale.ROOT));
  }
}
