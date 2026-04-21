package daycare.utils;

import java.util.Optional;
import java.util.Scanner;

/**
 * ScannerInput: parse only wrapper around a {@link Scanner}.
 *
 * <p>each method reads ONE line, tries to parse it as the requested type, and
 * returns {@code Optional.empty()} on parse failure or end of stream. never
 * prints, never retries. all UX should live in the caller.
 */
public class ScannerInput {

  private final Scanner scanner;

  public ScannerInput(Scanner scanner) {
    this.scanner = scanner;
  }

  public Optional<String> readLine() {
    if (!scanner.hasNextLine()) {
      return Optional.empty();
    }
    return Optional.of(scanner.nextLine().trim());
  }

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

  /** accepts y / yes / true / t for true and n / no / false / f for false, case insensitive. */
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

  public Optional<Character> readChar() {
    Optional<String> line = readLine();
    if (line.isEmpty() || line.get().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(line.get().charAt(0));
  }
}
