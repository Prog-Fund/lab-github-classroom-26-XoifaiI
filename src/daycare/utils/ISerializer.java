package daycare.utils;

/**
 * ISerializer: contract for anything that can persist itself to a file and read itself back.
 *
 * <p>both PetsDayCareAPI and OwnerAPI implement this so the Driver can call
 * {@code save()} / {@code load()} on either one without caring about the
 * concrete file format. the actual format we use here is XML via XStream,
 * but the interface doesnt commit to that, swapping in JSON/CSV later
 * wouldnt require touching the contract.
 *
 * <p>methods throw {@code Exception} bc the underlying io can blow up in a
 * dozen different ways (file missing, permission denied, malformed xml,
 * class-cast on load, etc) and the caller usually just wants one catch.
 *
 * <p>Returns: interface, implement it on a controller class.
 *
 * <p>Example:
 * <pre>{@code
 * public class PetsDayCareAPI implements ISerializer {
 *   public String fileName() { return "pets.xml"; }
 *   public void save() throws Exception { ... }
 *   public void load() throws Exception { ... }
 * }
 * }</pre>
 */
public interface ISerializer {

  /** Path or filename this serializer reads from / writes to. */
  String fileName();

  /** Writes the current in-memory state out to {@link #fileName()}. */
  void save() throws Exception;

  /** Replaces the current in-memory state with whatever is on disk at {@link #fileName()}. */
  void load() throws Exception;
}
