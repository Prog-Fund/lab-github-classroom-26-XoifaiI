package daycare.utils;

/** contract for anything that can persist itself to a file and read itself back. */
public interface ISerializer {

  String fileName();

  void save() throws Exception;

  void load() throws Exception;
}
