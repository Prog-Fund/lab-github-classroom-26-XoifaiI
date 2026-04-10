package daycare.tui;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel: Reusable bordered panel built from box drawing chars.
 *
 * <p>collect a bunch of elements (rows, centered lines, dividers), call
 * {@link #print()}, get a nice box. all padding goes through
 * {@link Tui#visibleLength(String)} so ansi codes inside the text dont throw
 * the alignment off.
 *
 * <p>Returns: builder, chain {@code row} / {@code centered} / {@code divider}
 * / {@code blank} then call {@link #print()} to dump it to stdout.
 *
 * <p>Example:
 * <pre>{@code
 * new Panel(40)
 *     .centered(Tui.bold("Hello"))
 *     .divider()
 *     .row("  left aligned line")
 *     .row("  another one")
 *     .blank()
 *     .print();
 * }</pre>
 */
public final class Panel {

  // single-line box drawing only. cp437 has these so they render under the
  // default windows console code page, the rounded ones (╭╮╰╯) get replaced
  // with '?' and look horrible. learned the hard way.
  private static final char H = '\u2500';  // ─
  private static final char TL = '\u250c'; // ┌
  private static final char TR = '\u2510'; // ┐
  private static final char BL = '\u2514'; // └
  private static final char BR = '\u2518'; // ┘
  private static final char ML = '\u251c'; // ├
  private static final char MR = '\u2524'; // ┤
  private static final char V = '\u2502';  // │

  private final int width;
  private final List<Element> elements = new ArrayList<>();

  public Panel(int width) {
    this.width = width;
  }

  /** Adds a left-aligned row. gets padded with spaces or chopped to fit. */
  public Panel row(String text) {
    elements.add(new Element(Kind.ROW, text));
    return this;
  }

  /** Adds a row centered horizontally inside the box. */
  public Panel centered(String text) {
    elements.add(new Element(Kind.CENTERED, text));
    return this;
  }

  /** Adds a horizontal divider line that spans the box. */
  public Panel divider() {
    elements.add(new Element(Kind.DIVIDER, ""));
    return this;
  }

  /** Adds an empty padded row, useful for breathing room. */
  public Panel blank() {
    return row("");
  }

  /** Dumps the whole panel to stdout. */
  public void print() {
    System.out.println(Tui.muted(top()));
    for (Element e : elements) {
      switch (e.kind()) {
        case ROW -> System.out.println(rowLine(e.text()));
        case CENTERED -> System.out.println(centeredLine(e.text()));
        case DIVIDER -> System.out.println(Tui.muted(dividerLine()));
      }
    }
    System.out.println(Tui.muted(bottom()));
  }

  private String top() {
    return TL + repeat(H, width - 2) + TR;
  }

  private String bottom() {
    return BL + repeat(H, width - 2) + BR;
  }

  private String dividerLine() {
    return ML + repeat(H, width - 2) + MR;
  }

  private String rowLine(String text) {
    int inner = width - 2;
    int visible = Tui.visibleLength(text);
    String body;
    if (visible > inner) {
      body = truncate(text, inner);
    } else {
      body = text + " ".repeat(inner - visible);
    }
    return Tui.muted(String.valueOf(V)) + body + Tui.muted(String.valueOf(V));
  }

  private String centeredLine(String text) {
    int inner = width - 2;
    int visible = Tui.visibleLength(text);
    int pad = Math.max(0, (inner - visible) / 2);
    String body =
        " ".repeat(pad) + text + " ".repeat(Math.max(0, inner - pad - visible));
    return Tui.muted(String.valueOf(V)) + body + Tui.muted(String.valueOf(V));
  }

  private static String truncate(String text, int maxVisible) {
    // walk the string char by char, copy ansi escapes through whole so the
    // styling survives the chop. assumes nobody styled half a character which
    // is fine for how we use it.
    String out = "";
    int visible = 0;
    int i = 0;
    while (i < text.length() && visible < maxVisible - 1) {
      char c = text.charAt(i);
      if (c == '\u001B' && i + 1 < text.length() && text.charAt(i + 1) == '[') {
        // hit an ansi escape, find the closing 'm' and copy the whole thing
        int end = text.indexOf('m', i);
        if (end >= 0) {
          out += text.substring(i, end + 1);
          i = end + 1;
          continue;
        }
      }
      out += c;
      visible++;
      i++;
    }
    return out + "...";
  }

  private static String repeat(char c, int n) {
    return String.valueOf(c).repeat(n);
  }

  private enum Kind {
    ROW,
    CENTERED,
    DIVIDER
  }

  private record Element(Kind kind, String text) {}
}
