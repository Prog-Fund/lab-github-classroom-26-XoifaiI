package daycare.tui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Tui: pure string helpers for the daycare TUI. never prints, never reads.
 *
 * <p>terminal width, ansi styling, escape sequences, sanitising. callers take
 * the returned strings and decide when to write them, so Driver stays the
 * only place that touches stdout.
 *
 * <p>Returns: static helpers only, dont instantiate. color on/off and stdout
 * encoding flip global state so should get called once at startup.
 */
public final class Tui {

  private static final String ESC = String.valueOf((char) 0x1B);
  private static final Pattern ANSI = Pattern.compile("\\x1B\\[[;\\d]*m");

  private static final int DEFAULT_WIDTH = 52;
  private static final int MIN_WIDTH = 40;
  private static final int MAX_WIDTH = 100;

  private static boolean colorEnabled = false;

  private Tui() {}

  public static void enableColor() {
    colorEnabled = true;
  }

  public static void disableColor() {
    colorEnabled = false;
  }

  public static boolean isColorEnabled() {
    return colorEnabled;
  }

  /** clamped terminal width from COLUMNS env var, falls back to a default. */
  public static int width() {
    String env = System.getenv("COLUMNS");
    if (env == null) {
      return DEFAULT_WIDTH;
    }
    try {
      int parsed = Integer.parseInt(env.trim());
      return Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, parsed));
    } catch (NumberFormatException e) {
      return DEFAULT_WIDTH;
    }
  }

  /** returns the screen clear escape when color is on, or an empty string. */
  public static String clearSequence() {
    return colorEnabled ? ESC + "[2J" + ESC + "[H" : "";
  }

  /** label caller should print before reading a line to implement "press enter to continue". */
  public static String pausePrompt() {
    return "  " + dim("Press Enter to continue... ");
  }

  /** should get called once at startup if the terminal does utf-8. */
  public static void enableUtf8Output() {
    System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
  }

  /** on-screen width of {@code s}, ignoring embedded ansi escapes. */
  public static int visibleLength(String s) {
    return ANSI.matcher(s).replaceAll("").length();
  }

  /**
   * strips control chars that would mess with terminal rendering.
   *
   * <p>user data gets echoed straight to stdout (list screens, status line),
   * so a name like ESC[2J could wipe the screen, redefine keybinds, or forge
   * a fake prompt. same threat applies to strings loaded from a hand edited
   * xml file, so callers should sanitize at both input and output boundaries.
   */
  public static String sanitize(String s) {
    if (s == null) {
      return null;
    }
    String out = "";
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      boolean strip = c == 0x7F
          || (c < 0x20 && c != '\t' && c != '\n')
          || (c >= 0x80 && c <= 0x9F);
      if (!strip) {
        out += c;
      }
    }
    return out;
  }

  public static String bold(String s) {
    return colorEnabled ? ESC + "[1m" + s + ESC + "[0m" : s;
  }

  public static String dim(String s) {
    return colorEnabled ? ESC + "[2m" + s + ESC + "[0m" : s;
  }

  public static String accent(String s) {
    return colorEnabled ? ESC + "[36m" + s + ESC + "[0m" : s;
  }

  public static String muted(String s) {
    return colorEnabled ? ESC + "[90m" + s + ESC + "[0m" : s;
  }
}
