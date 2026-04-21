package daycare.main;

import daycare.tui.Banner;
import daycare.tui.MenuItem;
import daycare.tui.Panel;
import daycare.tui.Tui;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu: composable menu screen for the daycare TUI. never prints.
 *
 * <p>build one with a title, maybe a breadcrumb, some sections of
 * {@link MenuItem}s, optionally a status line / hint / banner, then call
 * {@link #lines()} to get the rendered strings. caller prints them and reads
 * the users input, this class doesnt touch stdout or stdin.
 */
public final class Menu {

  private static final int BANNER_MIN_WIDTH = 60;

  private final String title;
  private String subtitle;
  private String breadcrumb;
  private String status;
  private String hint;
  private boolean banner;
  private int widthOverride;
  private final List<Section> sections = new ArrayList<>();
  private final List<MenuItem> trailing = new ArrayList<>();

  public Menu(String title) {
    this.title = title;
  }

  public Menu subtitle(String subtitle) {
    this.subtitle = subtitle;
    return this;
  }

  public Menu breadcrumb(String breadcrumb) {
    this.breadcrumb = breadcrumb;
    return this;
  }

  public Menu status(String status) {
    // status usually holds user typed names from the last action and gets
    // rendered straight to stdout, so sanitise here to keep the escape
    // injection hole closed without trusting every caller.
    this.status = Tui.sanitize(status);
    return this;
  }

  public Menu hint(String hint) {
    this.hint = hint;
    return this;
  }

  public Menu showBanner(boolean show) {
    this.banner = show;
    return this;
  }

  /** forces a specific panel width, ignoring {@link Tui#width()}. */
  public Menu width(int width) {
    this.widthOverride = width;
    return this;
  }

  public Menu section(String header, List<MenuItem> items) {
    sections.add(new Section(header, items));
    return this;
  }

  public Menu item(MenuItem item) {
    trailing.add(item);
    return this;
  }

  /** rendered lines for the whole menu screen, ready for the caller to print. */
  public List<String> lines() {
    List<String> out = new ArrayList<>();
    int width = widthOverride > 0 ? widthOverride : Tui.width();

    out.add("");
    if (breadcrumb != null) {
      out.add("  " + Tui.muted(breadcrumb));
    }
    if (banner && width >= BANNER_MIN_WIDTH) {
      out.addAll(Banner.daycare());
    }

    Panel panel = new Panel(width);
    panel.centered(Tui.bold(title));
    if (subtitle != null) {
      panel.centered(Tui.dim(subtitle));
    }
    for (Section s : sections) {
      panel.divider();
      panel.row("  " + Tui.accent(s.header().toUpperCase()));
      for (MenuItem entry : s.items()) {
        panel.row(formatItem(entry));
      }
    }
    if (!trailing.isEmpty()) {
      panel.divider();
      for (MenuItem entry : trailing) {
        panel.row(formatItem(entry));
      }
    }
    out.addAll(panel.lines());

    if (status != null) {
      out.add("  " + Tui.dim(status));
    }
    if (hint != null) {
      out.add("  " + Tui.muted(hint));
    }
    return out;
  }

  private static String formatItem(MenuItem item) {
    return String.format(
        "   %s   %s", Tui.bold(String.format("%2d", item.number())), item.label());
  }

  private record Section(String header, List<MenuItem> items) {}
}
