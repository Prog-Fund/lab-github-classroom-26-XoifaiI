package daycare.tui;

/**
 * MenuItem: One selectable menu entry.
 *
 * <p>just a pair, the number the user types to pick it and the label they
 * actually see on screen. zero behaviour, Menu does all the rendering.
 *
 * <p>Returns: record. {@code number} is the int the user types, {@code label}
 * is the human readable text shown next to it.
 *
 * <p>Example:
 * <pre>{@code
 * MenuItem exit = new MenuItem(0, "Exit");
 * int n = exit.number();      // 0
 * String l = exit.label();    // "Exit"
 * }</pre>
 */
public record MenuItem(int number, String label) {}
