package daycare.tui;

import java.util.ArrayList;
import java.util.List;

/**
 * Table: auto sized columns with a bordered header. never prints.
 *
 * <p>each column grows to fit its widest cell, capped by
 * {@link #maxColumnWidth(int)}. doesnt know what the cells mean.
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

  /** rendered lines ready for the caller to print. */
  public List<String> lines() {
    int cols = headers.size();
    int[] widths = new int[cols];
    for (int i = 0; i < cols; i++) {
      widths[i] = Math.min(maxColumnWidth, Tui.visibleLength(headers.get(i)));
    }
    for (List<String> row : rows) {
      for (int i = 0; i < cols && i < row.size(); i++) {
        widths[i] = Math.min(maxColumnWidth, Math.max(widths[i], Tui.visibleLength(row.get(i))));
      }
    }

    List<String> out = new ArrayList<>(rows.size() + 4);
    out.add(border(widths, '┌', '┬', '┐'));
    out.add(rowLine(headers, widths, true));
    out.add(border(widths, '├', '┼', '┤'));
    for (List<String> row : rows) {
      out.add(rowLine(row, widths, false));
    }
    out.add(border(widths, '└', '┴', '┘'));
    return out;
  }

  private static String border(int[] widths, char left, char mid, char right) {
    String line = Tui.muted(String.valueOf(left));
    for (int i = 0; i < widths.length; i++) {
      line += Tui.muted(repeat('─', widths[i] + 2));
      line += Tui.muted(String.valueOf(i == widths.length - 1 ? right : mid));
    }
    return line;
  }

  private static String rowLine(List<String> cells, int[] widths, boolean header) {
    String line = Tui.muted("│");
    for (int i = 0; i < widths.length; i++) {
      String cell = fit(i < cells.size() ? cells.get(i) : "", widths[i]);
      if (header) {
        cell = Tui.bold(cell);
      }
      line += " " + cell + " " + Tui.muted("│");
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
