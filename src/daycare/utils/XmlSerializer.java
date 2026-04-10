package daycare.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

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

  /** Writes {@code obj} to {@code file} as XML. */
  public static void saveToFile(Object obj, File file) throws Exception {
    Object xstream = createXStream();
    Method toXML = xstream.getClass().getMethod("toXML", Object.class, Writer.class);
    try (Writer writer = new FileWriter(file)) {
      toXML.invoke(xstream, obj, writer);
    }
  }

  /**
   * Reads an XML file written by {@link #saveToFile(Object, File)} and returns the object.
   *
   * <p>{@code allowedTypes} is xstreams security allowlist (required since
   * 1.4.18). pass every concrete class that might appear in the saved object
   * graph plus the collection wrapper classes.
   */
  public static Object loadFromFile(File file, Class<?>... allowedTypes) throws Exception {
    Object xstream = createXStream();
    if (allowedTypes.length > 0) {
      Method allowTypes = xstream.getClass().getMethod("allowTypes", Class[].class);
      allowTypes.invoke(xstream, (Object) allowedTypes);
    }
    Method fromXML = xstream.getClass().getMethod("fromXML", Reader.class);
    try (Reader reader = new FileReader(file)) {
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
