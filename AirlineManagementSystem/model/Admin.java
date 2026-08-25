package model;

public class Admin {
    private int adminId;
    private String username;
    private String password;
    private String name;
    private String email;

    public Admin() {}

    public Admin(int adminId, String username, String password, String name, String email) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public Admin(String username, String password, String name, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
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

    @Override
    public String toString() {
        return "Admin [ID=" + adminId + ", Username=" + username + ", Name=" + name + ", Email=" + email + "]";
    }
}
