package daycare.tui;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel: bordered box built from box drawing chars. never prints.
 *
 * <p>collect rows, centered lines, dividers, then call {@link #lines()} to get
 * the rendered strings. padding goes through {@link Tui#visibleLength(String)}
 * so ansi codes inside the text dont throw the alignment off.
 */
public final class Panel {

  // cp437 compatible single line chars, the rounded variants render as '?' on
  // the default windows console code page.
  private static final char H = '─';
  private static final char TL = '┌';
  private static final char TR = '┐';
  private static final char BL = '└';
  private static final char BR = '┘';
  private static final char ML = '├';
  private static final char MR = '┤';
  private static final char V = '│';

  private static final char ESC = (char) 0x1B;

  private final int width;
  private final List<Element> elements = new ArrayList<>();

  public Panel(int width) {
    this.width = width;
  }

  public Panel row(String text) {
    elements.add(new Element(Kind.ROW, text));
    return this;
  }

  public Panel centered(String text) {
    elements.add(new Element(Kind.CENTERED, text));
    return this;
  }

  public Panel divider() {
    elements.add(new Element(Kind.DIVIDER, ""));
    return this;
  }

  public Panel blank() {
    return row("");
  }

  /** rendered lines ready for the caller to print. */
  public List<String> lines() {
    List<String> out = new ArrayList<>(elements.size() + 2);
    out.add(Tui.muted(top()));
    for (Element e : elements) {
      switch (e.kind()) {
        case ROW -> out.add(rowLine(e.text()));
        case CENTERED -> out.add(centeredLine(e.text()));
        case DIVIDER -> out.add(Tui.muted(dividerLine()));
      }
    }
    out.add(Tui.muted(bottom()));
    return out;
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
    String body = visible > inner
        ? truncate(text, inner)
        : text + " ".repeat(inner - visible);
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

  // copies ansi escapes through whole so styling survives the chop. assumes
  // nobody styled half a character.
  private static String truncate(String text, int maxVisible) {
    String out = "";
    int visible = 0;
    int i = 0;
    while (i < text.length() && visible < maxVisible - 1) {
      char c = text.charAt(i);
      if (c == ESC && i + 1 < text.length() && text.charAt(i + 1) == '[') {
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
