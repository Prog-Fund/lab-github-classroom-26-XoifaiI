package daycare.models;

/**
 * Owner: a person who owns one or more pets at the daycare.
 *
 * <p>plain data class. id is assigned by OwnerAPI when the owner gets added,
 * not picked by the user. everything else is the contact info we need to
 * actually reach them when their dog eats the curtains.
 *
 * <p>Returns: instance of Owner with id + name + address + phone + email.
 *
 * <p>Example:
 * <pre>{@code
 * Owner alice = new Owner(1, "Alice Smith", "12 Main St",
 *     "555-0100", "alice@example.com");
 * String who = alice.getName();
 * alice.setPhone("555-0199");
 * }</pre>
 */
public class Owner {

  private int id;
  private String name;
  private String address;
  private String phone;
  private String email;

  public Owner(int id, String name, String address, String phone, String email) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.phone = phone;
    this.email = email;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String toString() {
    return String.format(
        "Owner{id=%d, name=%s, address=%s, phone=%s, email=%s}",
        id, name, address, phone, email);
  }
}
