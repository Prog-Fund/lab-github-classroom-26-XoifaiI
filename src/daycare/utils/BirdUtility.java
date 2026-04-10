package daycare.utils;

import java.util.List;

/**
 * BirdUtility: static helpers for bird related lookups.
 *
 * <p>two responsibilities right now:
 * <ul>
 *   <li>list of bird species the daycare accepts (mostly parrot species, the
 *       class is named generically so other birds can pile on later)</li>
 *   <li>turn a parrot's raw word count into a category String like "Basic" or
 *       "Amazing", which is what {@code Parrot.vocabularySize} actually
 *       stores per spec</li>
 * </ul>
 *
 * <p>Returns: static helpers only, dont instantiate.
 *
 * <p>Example:
 * <pre>{@code
 * String tier = BirdUtility.vocabularyCategory(45); // "Intermediate"
 * boolean known = BirdUtility.isKnownVocabularyCategory("Amazing"); // true
 * List<String> species = BirdUtility.getAllSpecies();
 * }</pre>
 */
public final class BirdUtility {

  /** vocab category used when the caller didnt give us a usable word count. */
  public static final String DEFAULT_VOCAB_CATEGORY = "Amazing";

  // category boundaries are inclusive on the lower end. tweak the numbers if
  // anita ever decides "amazing" should require a bigger vocabulary.
  private static final int BASIC_MIN = 0;
  private static final int INTERMEDIATE_MIN = 10;
  private static final int ADVANCED_MIN = 30;
  private static final int AMAZING_MIN = 60;

  private static final List<String> CATEGORIES =
      List.of("Basic", "Intermediate", "Advanced", "Amazing");

  private static final List<String> SPECIES = List.of(
      "African Grey",
      "Macaw",
      "Budgerigar",
      "Cockatoo",
      "Cockatiel",
      "Lovebird",
      "Conure",
      "Amazon",
      "Quaker Parrot",
      "Eclectus");

  private BirdUtility() {}

  /**
   * Maps a raw word count to its category String.
   *
   * <p>negative inputs are nonsense, fall back to {@link #DEFAULT_VOCAB_CATEGORY}.
   * positive inputs walk the bands {@code Basic / Intermediate / Advanced / Amazing}.
   */
  public static String vocabularyCategory(int words) {
    if (words < BASIC_MIN) {
      return DEFAULT_VOCAB_CATEGORY;
    }
    if (words >= AMAZING_MIN) {
      return "Amazing";
    }
    if (words >= ADVANCED_MIN) {
      return "Advanced";
    }
    if (words >= INTERMEDIATE_MIN) {
      return "Intermediate";
    }
    return "Basic";
  }

  /** Returns true if {@code category} is one of the known vocab tiers. case insensitive. */
  public static boolean isKnownVocabularyCategory(String category) {
    if (category == null) {
      return false;
    }
    for (String c : CATEGORIES) {
      if (c.equalsIgnoreCase(category)) {
        return true;
      }
    }
    return false;
  }

  /** Every vocab category, in order from quietest to chattiest. */
  public static List<String> getAllVocabularyCategories() {
    return CATEGORIES;
  }

  /** Every species in the list. */
  public static List<String> getAllSpecies() {
    return SPECIES;
  }

  /** Returns true if {@code species} is recognised (case sensitive). */
  public static boolean isKnownSpecies(String species) {
    return SPECIES.contains(species);
  }
}
