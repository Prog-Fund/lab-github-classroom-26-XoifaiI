package daycare.main;

import daycare.tui.Banner;
import daycare.tui.MenuItem;
import daycare.tui.Panel;
import daycare.tui.Tui;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Menu: Composable menu screen for the daycare TUI.
 *
 * <p>build one with a title, maybe a breadcrumb, some sections of
 * {@link MenuItem}s, optionally a status line / hint / banner, then call
 * {@link #prompt(Scanner)}. it draws the screen and hands u back whatever the
 * user typed. raw string. zero validation. u get to decide what "1" means and
 * what to do when they type "asdf".
 *
 * <p>Returns: builder, chain configuration methods then call
 * {@link #prompt(Scanner)} which draws the menu and returns the trimmed line
 * the user typed (possibly empty).
 *
 * <p>Example:
 * <pre>{@code
 * Scanner in = new Scanner(System.in);
 * String choice = new Menu("Doggie Day Care")
 *     .subtitle("main menu")
 *     .breadcrumb("Home > Main Menu")
 *     .showBanner(true)
 *     .width(60)
 *     .section("Manage", List.of(
 *         new MenuItem(1, "Dogs"),
 *         new MenuItem(2, "Reports")))
 *     .item(new MenuItem(0, "Exit"))
 *     .status("12 dogs loaded")
 *     .hint("[0] to exit")
 *     .prompt(in);
 * switch (choice) {
 *   case "0" -> System.exit(0);
 *   case "1" -> openDogs();
 *   ...
 * }
 * }</pre>
 */
public final class Menu {

  private static final int BANNER_MIN_WIDTH = 60;

  private final String title;
  private String subtitle;
  private String breadcrumb;
  private String status;
  private String hint;
  private boolean banner;
  private int widthOverride; // 0 means "no override, ask Tui.width()"
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
    // scrub control chars: status usually includes user-typed names from the
    // last action ("added owner Alice (id 3)"), and we render it through
    // Tui.dim straight to stdout. sanitising here keeps the escape injection
    // hole closed without having to trust every caller.
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

  /** Forces a specific panel width, ignoring whatever {@link Tui#width()} would say. */
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

  /**
   * Renders the menu and returns whatever the user typed.
   *
   * <p>trimmed, possibly empty, possibly nonsense. its on u to figure out what
   * to do with it.
   */
  public String prompt(Scanner in) {
    render();
    System.out.print("  " + Tui.accent(">") + " ");
    return in.hasNextLine() ? in.nextLine().trim() : "";
  }

  private void render() {
    int width = widthOverride > 0 ? widthOverride : Tui.width();
    System.out.println();
    if (breadcrumb != null) {
      System.out.println("  " + Tui.muted(breadcrumb));
    }
    if (banner && width >= BANNER_MIN_WIDTH) {
      for (String line : Banner.daycare()) {
        System.out.println(line);
      }
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
    panel.print();
    if (status != null) {
      System.out.println("  " + Tui.dim(status));
    }
    if (hint != null) {
      System.out.println("  " + Tui.muted(hint));
    }
  }

  private static String formatItem(MenuItem item) {
    return String.format(
        "   %s   %s", Tui.bold(String.format("%2d", item.number())), item.label());
  }

  private record Section(String header, List<MenuItem> items) {}

  /**
   * Demo entry point, run this file directly to see every Menu feature at once.
   *
   * <p>flags:
   * <ul>
   *   <li>{@code --no-color} : turn ansi styling off (its on by default in the demo)
   *   <li>{@code --utf8}     : switch stdout to utf-8 so fancier glyphs work, run
   *       {@code chcp 65001} first if ur on powershell
   * </ul>
   */
  public static void main(String[] args) {
    boolean color = true;
    boolean utf8 = false;
    for (String arg : args) {
      if ("--no-color".equals(arg)) {
        color = false;
      } else if ("--utf8".equals(arg)) {
        utf8 = true;
      }
    }
    if (utf8) {
      Tui.enableUtf8Output();
    }
    if (color) {
      Tui.enableColor();
    }
    Tui.clear();

    Scanner in = new Scanner(System.in);
    String choice = new Menu("Doggie Day Care")
        .subtitle("management console v0.1")     // dim line under the title
        .breadcrumb("Home > Main Menu")          // muted path above the box
        .width(60)                               // wider so the banner fits
        .showBanner(true)                        // 3-line ascii art header
        .section("Manage", List.of(              // section with header + items
            new MenuItem(1, "Dog Management"),
            new MenuItem(2, "Reports"),
            new MenuItem(3, "Search")))
        .section("Lookup", List.of(
            new MenuItem(6, "Find a Dog"),
            new MenuItem(7, "Find an Owner"),
            new MenuItem(8, "Calculate Weekly Income")))
        .section("Data", List.of(
            new MenuItem(20, "Save All"),
            new MenuItem(21, "Load All")))
        .item(new MenuItem(0, "Exit"))           // trailing item, no section header
        .status("12 dogs loaded, ready to go")   // dim line under the box
        .hint("[0] to exit, anything else just echoes back") // muted help line
        .prompt(in);                             // returns whatever the user typed

    System.out.println();
    System.out.println("  you typed: " + Tui.bold(choice));
    System.out.println();
    Tui.pause(in);
  }
}
