package daycare.utils;

/**
 * Utilities: tiny formatting + range helpers used across the codebase.
 *
 * <p>this is the dumping ground for one liners that dont fit anywhere else.
 * if it grows past ~10 methods or starts having internal state, split it
 * into a more focused class instead of letting it become a kitchen sink.
 *
 * <p>Returns: static helpers only, dont instantiate.
 *
 * <p>Example:
 * <pre>{@code
 * String price = Utilities.formatMoney(45.0);          // "$45.00"
 * String day = Utilities.dayName(0);                    // "Mon"
 * String label = Utilities.pluralize(3, "dog", "dogs"); // "3 dogs"
 * boolean ok = Utilities.validRange(5, 0, 10);          // true
 * }</pre>
 */
public final class Utilities {

  /** Currency symbol prepended by {@link #formatMoney(double)}. ASCII so it works in cp437. */
  public static final String CURRENCY_SYMBOL = "$";

  // mon..sat only, urban tails is closed sundays so theres no slot for it.
  private static final String[] DAY_NAMES =
      {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

  private Utilities() {}

  /** Formats {@code amount} as a currency string with two decimals. */
  public static String formatMoney(double amount) {
    return String.format("%s%.2f", CURRENCY_SYMBOL, amount);
  }

  /** Returns the short name for {@code day} (0 = Mon, 5 = Sat). out-of-range -> "?". */
  public static String dayName(int day) {
    if (day < 0 || day >= DAY_NAMES.length) {
      return "?";
    }
    return DAY_NAMES[day];
  }

  /**
   * Formats a 6-slot daysAttending array as a comma separated list of day names.
   *
   * <p>e.g. {@code [true, true, false, true, false, false]} -> "Mon, Tue, Thu".
   * empty schedules return "(none)" so the menu doesnt show a dangling colon.
   */
  public static String formatDaysAttending(boolean[] daysAttending) {
    String result = "";
    for (int i = 0; i < daysAttending.length && i < DAY_NAMES.length; i++) {
      if (daysAttending[i]) {
        if (!result.isEmpty()) {
          result += ", ";
        }
        result += DAY_NAMES[i];
      }
    }
    return result.isEmpty() ? "(none)" : result;
  }

  /** "1 dog" / "2 dogs" / "0 dogs" : picks singular when count == 1, plural otherwise. */
  public static String pluralize(int count, String singular, String plural) {
    return count + " " + (count == 1 ? singular : plural);
  }

  /** Returns true if {@code value} is in {@code [min, max]} inclusive. */
  public static boolean validRange(int value, int min, int max) {
    return value >= min && value <= max;
  }
}
