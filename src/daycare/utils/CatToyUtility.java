package daycare.utils;

import java.util.List;

/** static suggestion list for Cat.favouriteToy. the model accepts anything, this is just hints. */
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

  public static List<String> getAllToys() {
    return TOYS;
  }

  public static boolean isKnownToy(String toy) {
    return TOYS.contains(toy);
  }
}
