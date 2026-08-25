package model;

public class Customer {
    private int customerId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String passportNumber;

    public Customer() {}

    public Customer(int customerId, String username, String password, String name, String email, String phone, String passportNumber) {
        this.customerId = customerId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passportNumber = passportNumber;
    }

    public Customer(String username, String password, String name, String email, String phone, String passportNumber) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passportNumber = passportNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    @Override
    public String toString() {
        return "Customer [ID=" + customerId + ", Username=" + username + ", Name=" + name + ", Email=" + email + ", Phone=" + phone + ", Passport=" + passportNumber + "]";
    }
}
