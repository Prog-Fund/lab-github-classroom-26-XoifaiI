package daycare.models;

/** plain data class for a pet owner. id gets assigned by OwnerAPI, not picked by the user. */
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
