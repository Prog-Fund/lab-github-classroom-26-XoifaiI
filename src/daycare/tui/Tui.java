package daycare.tui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Tui: Shared TUI helpers for the daycare app.
 *
 * <p>terminal width, ansi styling, clearing the screen, pause to continue, that
 * kinda thing. strictly presentational, this class writes bytes and reads raw
 * lines, it never decides whether the input is "valid". thats the callers job.
 *
 * <p>Returns: static helpers only, dont instantiate. some methods flip global
 * state (color on/off, stdout encoding) so call those once at startup.
 *
 * <p>Example:
 * <pre>{@code
 * Tui.enableColor();           // ansi on for the rest of the run
 * Tui.enableUtf8Output();      // optional, if your terminal does utf-8
 * Tui.clear();                 // wipe the screen
 * System.out.println(Tui.bold("hello"));
 * Tui.pause(new Scanner(System.in));
 * }</pre>
 */
public final class Tui {

  /** matches a CSI ansi sequence so we can strip them when measuring widths. */
  private static final Pattern ANSI = Pattern.compile("\u001B\\[[;\\d]*m");

  private static final int DEFAULT_WIDTH = 52;
  private static final int MIN_WIDTH = 40;
  private static final int MAX_WIDTH = 100;

  private static boolean colorEnabled = false;

  private Tui() {}

  /** Turns ansi styling on for everything below. off by default bc not every console likes it. */
  public static void enableColor() {
    colorEnabled = true;
  }

  /** Turns ansi styling back off. */
  public static void disableColor() {
    colorEnabled = false;
  }

  public static boolean isColorEnabled() {
    return colorEnabled;
  }

  /**
   * Returns a usable terminal width.
   *
   * <p>reads the COLUMNS env var, clamps it to a sane range, otherwise falls
   * back to the default. windows almost never sets COLUMNS so this is mostly
   * the default unless ur running under bash / WT with it exported.
   */
  public static int width() {
    String env = System.getenv("COLUMNS");
    if (env == null) {
      return DEFAULT_WIDTH;
    }
    try {
      int parsed = Integer.parseInt(env.trim());
      return Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, parsed));
    } catch (NumberFormatException e) {
      // someone set COLUMNS to garbage. shrug, use the default
      return DEFAULT_WIDTH;
    }
  }

  /** Clears the screen, only if color is on (we reuse the same ansi assumption). */
  public static void clear() {
    if (colorEnabled) {
      // \033[2J wipes the screen, \033[H sends the cursor back home
      System.out.print("\u001B[2J\u001B[H");
      System.out.flush();
    }
  }

  /** Prints a press enter prompt and blocks until the user hits enter. */
  public static void pause(Scanner in) {
    System.out.print("  " + dim("Press Enter to continue... "));
    if (in.hasNextLine()) {
      in.nextLine();
    }
  }

  /**
   * Switches stdout to UTF-8.
   *
   * <p>call this once at startup if u want fancier glyphs (rounded corners,
   * smart quotes, accents in dog names). your terminal also has to be in utf-8
   * mode or it'll look like mojibake, on powershell run {@code chcp 65001}
   * first. on windows terminal ur usually fine already.
   */
  public static void enableUtf8Output() {
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
  }

  /** Returns how wide {@code s} actually looks, ignoring any ansi escape codes inside it. */
  public static int visibleLength(String s) {
    // ansi codes count toward String.length() but take 0 columns on screen, so
    // strip them before counting or all our padding math goes sideways
    return ANSI.matcher(s).replaceAll("").length();
  }

  public static String bold(String s) {
    return colorEnabled ? "\u001B[1m" + s + "\u001B[0m" : s;
  }

  public static String dim(String s) {
    return colorEnabled ? "\u001B[2m" + s + "\u001B[0m" : s;
  }

  public static String accent(String s) {
    return colorEnabled ? "\u001B[36m" + s + "\u001B[0m" : s;
  }

  public static String muted(String s) {
    return colorEnabled ? "\u001B[90m" + s + "\u001B[0m" : s;
  }
}
