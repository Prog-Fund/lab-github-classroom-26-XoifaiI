package daycare.controllers;

import daycare.models.Owner;
import daycare.utils.ISerializer;
import daycare.utils.Utilities;
import daycare.utils.XmlSerializer;
import java.io.File;
import java.util.ArrayList;

/**
 * OwnerAPI: in memory CRUD for the daycares owners list, persisted as XML.
 *
 * <p>id assignment: {@link #addOwner(Owner)} with id 0 picks a fresh id from
 * the rolling counter. on load the counter should get bumped past the largest
 * existing id so new owners dont collide with restored ones.
 */
public class OwnerAPI implements ISerializer {

  private static final Class<?>[] ALLOWED_TYPES = {
      Owner.class, ArrayList.class
  };

  private String name;
  private File file;
  private ArrayList<Owner> owners;
  private int nextOwnerId;

  public OwnerAPI(String name, File file) {
    this.name = name;
    this.file = file;
    this.owners = new ArrayList<>();
    this.nextOwnerId = 1;
  }

  public Owner addOwner(String name, String address, String phone, String email) {
    Owner owner = new Owner(nextOwnerId++, name, address, phone, email);
    owners.add(owner);
    return owner;
  }

  public boolean addOwner(Owner owner) {
    if (owner == null) {
      return false;
    }
    if (owner.getId() == 0) {
      owner.setId(nextOwnerId++);
    } else if (owner.getId() >= nextOwnerId) {
      nextOwnerId = owner.getId() + 1;
    }
    return owners.add(owner);
  }

  public Owner removeOwner(int index) {
    if (!isValidOwnerIndex(index)) {
      return null;
    }
    return owners.remove(index);
  }

  public Owner getOwner(int index) {
    if (!isValidOwnerIndex(index)) {
      return null;
    }
    return owners.get(index);
  }

  public Owner getOwnerByName(String name) {
    for (Owner o : owners) {
      if (o.getName().equalsIgnoreCase(name)) {
        return o;
      }
    }
    return null;
  }

  public Owner getOwnerById(int id) {
    for (Owner o : owners) {
      if (o.getId() == id) {
        return o;
      }
    }
    return null;
  }

  public boolean isValidOwnerIndex(int index) {
    return index >= 0 && index < owners.size();
  }

  public int numberOfOwners() {
    return owners.size();
  }

  public ArrayList<Owner> getOwners() {
    return new ArrayList<>(owners);
  }

  public String listAllOwners() {
    return Utilities.joinIndexed(owners, o -> true, "(no owners)");
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String fileName() {
    return file.getName();
  }

  @Override
  public void save() throws Exception {
    XmlSerializer.saveToFile(owners, file);
  }

  @Override
  public void load() throws Exception {
    if (!file.exists()) {
      return;
    }
    Object loaded = XmlSerializer.loadFromFile(file, ALLOWED_TYPES);
    if (!(loaded instanceof ArrayList<?> rawList)) {
      throw new IllegalStateException(
          "owners file root is not a List, got: "
              + (loaded == null ? "null" : loaded.getClass().getName()));
    }
    ArrayList<Owner> typed = new ArrayList<>(rawList.size());
    for (Object item : rawList) {
      if (!(item instanceof Owner owner)) {
        throw new IllegalStateException(
            "owners file contains a non-Owner entry: "
                + (item == null ? "null" : item.getClass().getName()));
      }
      typed.add(owner);
    }
    this.owners = typed;
    recomputeNextOwnerId();
  }

  private void recomputeNextOwnerId() {
    int max = 0;
    for (Owner o : owners) {
      if (o.getId() > max) {
        max = o.getId();
      }
    }
    nextOwnerId = max + 1;
  }
}
