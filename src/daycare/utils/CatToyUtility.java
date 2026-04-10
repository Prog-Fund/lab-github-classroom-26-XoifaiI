package daycare.utils;

import java.util.List;

/**
 * CatToyUtility: static list of common cat toys for the favouriteToy field.
 *
 * <p>used by the menu/forms layer when prompting the user to pick a toy. cats
 * are also free to bring their own (the favouriteToy field on Cat is just a
 * String, no enforcement), this list is just a starter set of suggestions.
 *
 * <p>Returns: static helpers only, dont instantiate.
 *
 * <p>Example:
 * <pre>{@code
 * List<String> toys = CatToyUtility.getAllToys();
 * boolean known = CatToyUtility.isKnownToy("Laser Pointer"); // true
 * }</pre>
 */
public final class CatToyUtility {

  private static final List<String> TOYS = List.of(
      "Feather Wand",
      "Laser Pointer",
      "Catnip Mouse",
      "Crinkle Ball",
      "Scratching Post",
      "Cardboard Box",
      "String",
      "Tunnel",
      "Jingle Ball",
      "Plush Mouse");

  private CatToyUtility() {}

  /** Every toy in the suggestion list. */
  public static List<String> getAllToys() {
    return TOYS;
  }

  /** Returns true if {@code toy} is in the suggestion list (case sensitive). */
  public static boolean isKnownToy(String toy) {
    return TOYS.contains(toy);
  }
}
