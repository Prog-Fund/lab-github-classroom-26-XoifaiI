package daycare.utils;

import java.util.Optional;
import java.util.Scanner;

/**
 * ScannerInput: parse-only wrapper around a {@link Scanner}.
 *
 * <p>each method reads ONE line, tries to parse it as the requested type,
 * and returns {@code Optional.empty()} on parse failure or end of stream.
 * theres no printing and no retry loop in here. all UX (prompts, error
 * messages, "try again" loops) is the main/Menu layers job, ScannerInput
 * just gives the caller a safe single shot parse step.
 *
 * <p>this split exists so the printing rules stay clean: Menu owns stdout,
 * utils never touches it.
 *
 * <p>Returns: instance, construct one wrapping a Scanner and reuse it.
 *
 * <p>Example:
 * <pre>{@code
 * ScannerInput in = new ScannerInput(new Scanner(System.in));
 *
 * single-shot read:
 * Optional<Integer> parsed = in.readInt();
 *
 * typical retry loop, lives in the main layer where printing is allowed:
 * Optional<Integer> result;
 * do {
 *   System.out.print("Age: ");
 *   result = in.readInt();
 *   if (result.isEmpty()) {
 *     System.out.println("not a whole number, try again");
 *   }
 * } while (result.isEmpty());
 * int age = result.get();
 * }</pre>
 */
public class ScannerInput {

  private final Scanner scanner;

  public ScannerInput(Scanner scanner) {
    this.scanner = scanner;
  }

  /** Reads one trimmed line. Empty if the stream ran out (ctrl+d / piped EOF). */
  public Optional<String> readLine() {
    if (!scanner.hasNextLine()) {
      return Optional.empty();
    }
    return Optional.of(scanner.nextLine().trim());
  }

  /** Reads one line and parses it as an int. */
  public Optional<Integer> readInt() {
    Optional<String> line = readLine();
    if (line.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(Integer.valueOf(line.get()));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  /** Reads one line and parses it as a double. */
  public Optional<Double> readDouble() {
    Optional<String> line = readLine();
    if (line.isEmpty()) {
      return Optional.empty();
    }
    try {
      return Optional.of(Double.valueOf(line.get()));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  /**
   * Reads one line and parses it as a boolean.
   *
   * <p>accepts y / yes / true / t for true and n / no / false / f for false,
   * case-insensitive. anything else (or EOF) is empty.
   */
  public Optional<Boolean> readBoolean() {
    Optional<String> line = readLine();
    if (line.isEmpty()) {
      return Optional.empty();
    }
    return switch (line.get().toLowerCase()) {
      case "y", "yes", "true", "t" -> Optional.of(true);
      case "n", "no", "false", "f" -> Optional.of(false);
      default -> Optional.empty();
    };
  }

  /** Reads one line and returns its first character. Empty on blank line or EOF. */
  public Optional<Character> readChar() {
    Optional<String> line = readLine();
    if (line.isEmpty() || line.get().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(line.get().charAt(0));
  }
}
