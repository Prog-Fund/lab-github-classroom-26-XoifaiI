package daycare.utils;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * XmlSerializer: reflection based wrapper around XStream for save/load.
 *
 * <p>uses reflection to call into XStream so this file (and the rest of the
 * project) compiles cleanly even when the xstream jar isnt on the classpath.
 * xstream is only needed at RUNTIME, drop xstream-1.4.21.jar in lib/ and
 * run with {@code -cp "out;lib/*"} (windows) / {@code "out:lib/*"} (unix)
 * and save/load just works. if the jar is missing youll get a clear error.
 *
 * <p>why reflection: lets the project compile without dragging in a
 * third-party dep at compile time. the cost is some ugly Method.invoke calls
 * in {@link #saveToFile(Object, File)} / {@link #loadFromFile(File, Class[])},
 * which is contained to this one file.
 *
 * <p>Returns: static helpers only, dont instantiate.
 *
 * <p>Example:
 * <pre>{@code
 * File file = new File("pets.xml");
 * ArrayList<Pet> pets = ...;
 * XmlSerializer.saveToFile(pets, file);
 *
 * Object loaded = XmlSerializer.loadFromFile(
 *     file, Pet.class, Dog.class, Cat.class, Parrot.class, ArrayList.class);
 * @SuppressWarnings("unchecked")
 * ArrayList<Pet> back = (ArrayList<Pet>) loaded;
 * }</pre>
 */
public final class XmlSerializer {

  private static final String XSTREAM_CLASS = "com.thoughtworks.xstream.XStream";
  private static final String DOM_DRIVER_CLASS = "com.thoughtworks.xstream.io.xml.DomDriver";
  private static final String DRIVER_INTERFACE =
      "com.thoughtworks.xstream.io.HierarchicalStreamDriver";

  private XmlSerializer() {}

  /**
   * Writes {@code obj} to {@code file} as UTF-8 XML, atomically.
   *
   * <p>writes to a sibling {@code .tmp} file first, then moves it into place
   * with ATOMIC_MOVE (falling back to a plain replace on filesystems that
   * dont support atomic moves). this way a crash mid-write leaves the old
   * file intact instead of truncating it to 0 bytes, which is what plain
   * {@code new FileWriter(file)} would do.
   *
   * <p>charset is pinned to UTF-8 on both sides (save and load) so non-ascii
   * owner names / addresses / toys survive round tripping across windows
   * (cp1252 default) and linux (utf-8 default).
   */
  public static void saveToFile(Object obj, File file) throws Exception {
    Object xstream = createXStream();
    Method toXML = xstream.getClass().getMethod("toXML", Object.class, Writer.class);
    Path target = file.toPath();
    Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
    try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
      toXML.invoke(xstream, obj, writer);
    } catch (Exception e) {
      // write failed mid-stream, clean up the partial tmp so it doesnt linger.
      // note: on success we leave tmp alone until the move below consumes it.
      try {
        Files.deleteIfExists(tmp);
      } catch (Exception ignored) {
        // best effort, not worth masking the original failure
      }
      throw e;
    }
    // tmp file has the full new contents, swap it into place
    try {
      Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    } catch (AtomicMoveNotSupportedException e) {
      // some windows + network filesystems dont support atomic move, fall back
      // to a non-atomic replace. still better than the old truncate then write.
      Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /**
   * Reads an XML file written by {@link #saveToFile(Object, File)} and returns the object.
   *
   * <p>{@code allowedTypes} is xstreams security allowlist (required since
   * 1.4.18). pass every concrete class that might appear in the saved object
   * graph plus the collection wrapper classes.
   *
   * <p>reader is pinned to UTF-8, matching what {@link #saveToFile(Object, File)}
   * writes. the platform default charset (cp1252 on windows) would otherwise
   * mangle any non ascii characters written on a utf-8 box.
   */
  public static Object loadFromFile(File file, Class<?>... allowedTypes) throws Exception {
    Object xstream = createXStream();
    if (allowedTypes.length > 0) {
      Method allowTypes = xstream.getClass().getMethod("allowTypes", Class[].class);
      allowTypes.invoke(xstream, (Object) allowedTypes);
    }
    Method fromXML = xstream.getClass().getMethod("fromXML", Reader.class);
    try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
      return fromXML.invoke(xstream, reader);
    }
  }

  private static Object createXStream() {
    try {
      Class<?> driverClass = Class.forName(DOM_DRIVER_CLASS);
      Object driver = driverClass.getDeclaredConstructor().newInstance();
      Class<?> driverInterface = Class.forName(DRIVER_INTERFACE);
      Class<?> xstreamClass = Class.forName(XSTREAM_CLASS);
      Constructor<?> ctor = xstreamClass.getConstructor(driverInterface);
      return ctor.newInstance(driver);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(
          "XStream not found on the classpath. Drop xstream-1.4.21.jar in lib/ and rerun"
              + " with -cp \"out;lib/*\" (windows) or \"out:lib/*\" (unix).",
          e);
    } catch (Exception e) {
      throw new IllegalStateException("failed to create XStream instance: " + e.getMessage(), e);
    }
  }
}
