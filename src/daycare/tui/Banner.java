package daycare.tui;

import java.util.ArrayList;
import java.util.List;

/**
 * Banner: ASCII banners for the home screen.
 *
 * <p>banners live ABOVE the panel not inside it, so they dont have to fit the
 * panel width. caller decides if the terminal is wide enough to bother showing
 * one ({@code daycare.Menu} currently gates on width >= 60).
 *
 * <p>Returns: static helpers only, dont instantiate. each method returns a
 * {@code List<String>} of pre-styled lines, one per row of art.
 *
 * <p>Example:
 * <pre>{@code
 * for (String line : Banner.daycare()) {
 *   System.out.println(line);
 * }
 * }</pre>
 */
public final class Banner {

  private Banner() {}
  
  public static List<String> daycare() {
    return styled(
        "  ╔╦╗ ┌─┐ ┌─┐ ┌─┐ ┬ ┌─┐    ╔╦╗ ┌─┐ ┬ ┬    ╔═╗ ┌─┐ ┬─┐ ┌─┐",
        "   ║║ │ │ │ ┬ │ ┬ │ ├┤      ║║ ├─┤ └┬┘    ║   ├─┤ ├┬┘ ├┤ ",
        "  ═╩╝ └─┘ └─┘ └─┘ ┴ └─┘    ═╩╝ ┴ ┴  ┴     ╚═╝ ┴ ┴ ┴└─ └─┘");
  }

  private static List<String> styled(String... lines) {
    List<String> out = new ArrayList<>(lines.length);
    for (String line : lines) {
      out.add(Tui.accent(line));
    }
    return out;
  }
}
