package daycare.utils;

import java.util.List;

/**
 * BirdUtility: static list of bird species the daycare accepts.
 *
 * <p>only Parrot is currently a concrete subclass of Bird, so this is mostly
 * a list of parrot species, but the class is named generically so other birds
 * can pile on later without renaming.
 *
 * <p>Returns: static helpers only, dont instantiate.
 *
 * <p>Example:
 * <pre>{@code
 * List<String> species = BirdUtility.getAllSpecies();
 * boolean known = BirdUtility.isKnownSpecies("African Grey"); // true
 * }</pre>
 */
public final class BirdUtility {

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

  /** Every species in the list. */
  public static List<String> getAllSpecies() {
    return SPECIES;
  }

  /** Returns true if {@code species} is recognised (case sensitive). */
  public static boolean isKnownSpecies(String species) {
    return SPECIES.contains(species);
  }
}
