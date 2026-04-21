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
 * XmlSerializer: reflection based wrapper around XStream for save and load.
 *
 * <p>reflection lets the project compile without xstream on the compile time
 * classpath. xstream is only needed at runtime, drop xstream-1.4.21.jar in
 * lib/ and run with {@code -cp "out;lib/*"} (windows) / {@code "out:lib/*"}
 * (unix).
 */
public final class XmlSerializer {

  private static final String XSTREAM_CLASS = "com.thoughtworks.xstream.XStream";
  private static final String DOM_DRIVER_CLASS = "com.thoughtworks.xstream.io.xml.DomDriver";
  private static final String DRIVER_INTERFACE =
      "com.thoughtworks.xstream.io.HierarchicalStreamDriver";

  private XmlSerializer() {}

  /**
   * writes {@code obj} to {@code file} as UTF-8 XML, atomically.
   *
   * <p>writes to a sibling {@code .tmp} first then moves it into place, so a
   * crash mid write leaves the old file intact instead of truncating it.
   * charset is pinned to UTF-8 on both sides so non ascii owner names survive
   * round tripping across windows and linux defaults.
   */
  public static void saveToFile(Object obj, File file) throws Exception {
    Object xstream = createXStream();
    Method toXML = xstream.getClass().getMethod("toXML", Object.class, Writer.class);
    Path target = file.toPath();
    Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
    try (Writer writer = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
      toXML.invoke(xstream, obj, writer);
    } catch (Exception e) {
      try {
        Files.deleteIfExists(tmp);
      } catch (Exception ignored) {
        // best effort, dont mask the original failure
      }
      throw e;
    }
    try {
      Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    } catch (AtomicMoveNotSupportedException e) {
      // some windows + network filesystems dont support atomic move
      Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /**
   * reads an XML file written by {@link #saveToFile(Object, File)}.
   *
   * <p>{@code allowedTypes} is xstreams security allowlist (required since
   * 1.4.18). callers should pass every concrete class that might appear in
   * the saved object graph plus the collection wrapper classes.
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
