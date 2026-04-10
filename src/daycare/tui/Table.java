package daycare.tui;

import java.util.ArrayList;
import java.util.List;

/**
 * Table: Renders a table with auto sized columns and a bordered header row.
 *
 * <p>each column grows to fit its widest cell, capped by
 * {@link #maxColumnWidth(int)}, anything longer gets the "..." treatment.
 * doesnt know what the cells mean.
 *
 * <p>Returns: builder, chain {@code addRow} / {@code maxColumnWidth} then call
 * {@link #print()} to dump it to stdout.
 *
 * <p>Example:
 * <pre>{@code
 * new Table(List.of("Id", "Name", "Breed"))
 *     .addRow(List.of("1", "Rex", "Labrador"))
 *     .addRow(List.of("2", "Biscuit", "Corgi"))
 *     .maxColumnWidth(20)
 *     .print();
 * }</pre>
 */
public final class Table {

  private final List<String> headers;
  private final List<List<String>> rows = new ArrayList<>();
  private int maxColumnWidth = 30;

  public Table(List<String> headers) {
    this.headers = List.copyOf(headers);
  }

  public Table addRow(List<String> row) {
    rows.add(List.copyOf(row));
    return this;
  }

  public Table maxColumnWidth(int max) {
    this.maxColumnWidth = max;
    return this;
  }

  /** Dumps the table to stdout. */
  public void print() {
    int cols = headers.size();
    int[] widths = new int[cols];
    // start each column wide enough for its header...
    for (int i = 0; i < cols; i++) {
      widths[i] = Math.min(maxColumnWidth, Tui.visibleLength(headers.get(i)));
    }
    // ...then grow to fit any wider cell, but never past the cap
    for (List<String> row : rows) {
      for (int i = 0; i < cols && i < row.size(); i++) {
        widths[i] = Math.min(maxColumnWidth, Math.max(widths[i], Tui.visibleLength(row.get(i))));
      }
    }
    System.out.println(border(widths, '\u250c', '\u252c', '\u2510'));
    System.out.println(rowLine(headers, widths, true));
    System.out.println(border(widths, '\u251c', '\u253c', '\u2524'));
    for (List<String> row : rows) {
      System.out.println(rowLine(row, widths, false));
    }
    System.out.println(border(widths, '\u2514', '\u2534', '\u2518'));
  }

  private static String border(int[] widths, char left, char mid, char right) {
    String line = Tui.muted(String.valueOf(left));
    for (int i = 0; i < widths.length; i++) {
      line += Tui.muted(repeat('\u2500', widths[i] + 2));
      line += Tui.muted(String.valueOf(i == widths.length - 1 ? right : mid));
    }
    return line;
  }

  private static String rowLine(List<String> cells, int[] widths, boolean header) {
    String line = Tui.muted("\u2502");
    for (int i = 0; i < widths.length; i++) {
      String cell = i < cells.size() ? cells.get(i) : "";
      cell = fit(cell, widths[i]);
      if (header) {
        cell = Tui.bold(cell);
      }
      line += " " + cell + " " + Tui.muted("\u2502");
    }
    return line;
  }

  private static String fit(String text, int width) {
    int visible = Tui.visibleLength(text);
    if (visible == width) {
      return text;
    }
    if (visible < width) {
      return text + " ".repeat(width - visible);
    }
    return text.substring(0, Math.max(0, width - 1)) + "...";
  }

  private static String repeat(char c, int n) {
    return String.valueOf(c).repeat(n);
  }
}
