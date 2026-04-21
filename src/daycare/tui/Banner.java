package daycare.tui;

import java.util.ArrayList;
import java.util.List;

/**
 * Banner: ASCII banners for the home screen. returns strings, never prints.
 *
 * <p>lives ABOVE the panel not inside it, so the art doesnt have to fit the
 * panel width. caller decides whether the terminal is wide enough to bother.
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
