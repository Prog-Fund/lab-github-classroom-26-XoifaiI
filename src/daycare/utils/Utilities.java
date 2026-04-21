package daycare.utils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** static formatting, text, and range helpers used across the codebase. */
public final class Utilities {

  public static final String CURRENCY_SYMBOL = "$";

  // mon..sat only, the daycare is closed sundays.
  private static final String[] DAY_NAMES =
      {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

  private Utilities() {}

  public static String formatMoney(double amount) {
    return String.format("%s%.2f", CURRENCY_SYMBOL, amount);
  }

  /** short name for {@code day} (0 = Mon, 5 = Sat). out of range returns "?". */
  public static String dayName(int day) {
    if (day < 0 || day >= DAY_NAMES.length) {
      return "?";
    }
    return DAY_NAMES[day];
  }

  /** formats a 6 slot daysAttending array as a comma separated list of day names. */
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

  /** picks singular when count == 1, plural otherwise. */
  public static String pluralize(int count, String singular, String plural) {
    return count + " " + (count == 1 ? singular : plural);
  }

  /** true if {@code value} is in {@code [min, max]} inclusive. */
  public static boolean validRange(int value, int min, int max) {
    return value >= min && value <= max;
  }

  /** chops {@code value} down to {@code max} chars. null in, null out. */
  public static String truncate(String value, int max) {
    if (value == null) {
      return null;
    }
    return value.length() <= max ? value : value.substring(0, max);
  }

  /** counts items in {@code list} that pass {@code filter}. */
  public static <T> int countWhere(Iterable<T> list, Predicate<? super T> filter) {
    int count = 0;
    for (T item : list) {
      if (filter.test(item)) {
        count++;
      }
    }
    return count;
  }

  /**
   * joins items that pass {@code filter} as {@code "index: item"} lines, where
   * the index is the items position in the original list so values line up
   * with what the user sees on screen and what index based deletes expect.
   */
  public static <T> String joinIndexed(
      List<T> list,
      Predicate<? super T> filter,
      String emptyMsg) {
    String result = IntStream.range(0, list.size())
        .filter(i -> filter.test(list.get(i)))
        .mapToObj(i -> i + ": " + list.get(i))
        .collect(Collectors.joining("\n"));
    return result.isEmpty() ? emptyMsg : result;
  }
}
