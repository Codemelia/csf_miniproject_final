package csf.finalmp.app.server.models.authentication;

// PURPOSE OF THIS MODEL
// SEPARATE CONCERNS FROM SERVER WHEN RECEIVING USER DETAILS FROM CLIENT

public class AuthRequest {
    
    // variables
    private String username;
    private String password;
    private String role;

    // constructors
    public AuthRequest() {}
    public AuthRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // to string
    @Override
    public String toString() {
        return "AuthRequest [username=" + username + ", password=" + password + ", role=" + role + "]";
    }

}
