package csf.finalmp.app.server.models;

// PURPOSE OF THIS MODEL
// CONV TO AUTH REQUEST TO SERVER SIDE USER OBJECT

public class User {

    // variables
    private Long id;
    private String username;
    private String password;
    private String role;

    // constructors
    public User() {}
    public User(Long id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }  

    // to string
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", password=" + password + ", role=" + role + "]";
    }  
    
}
