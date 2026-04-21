package daycare.utils;

import java.util.List;

/** static helpers for bird related lookups: accepted species and vocab category boundaries. */
public final class BirdUtility {

  public static final String DEFAULT_VOCAB_CATEGORY = "Amazing";

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

  /** maps a raw word count to its category string. negatives fall back to the default. */
  public static String vocabularyCategory(int words) {
    if (words < 0) {
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

  public static List<String> getAllVocabularyCategories() {
    return CATEGORIES;
  }

  public static List<String> getAllSpecies() {
    return SPECIES;
  }

  public static boolean isKnownSpecies(String species) {
    return SPECIES.contains(species);
  }
}
