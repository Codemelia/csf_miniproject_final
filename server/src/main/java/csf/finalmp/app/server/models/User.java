package csf.finalmp.app.server.models;

import java.time.LocalDateTime;
import java.util.UUID;

// CONV TO AUTH REQUEST TO SERVER SIDE USER OBJECT

public class User {

    // variables
    private String userId;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors
    public User() {
        this.userId = UUID.randomUUID().toString().substring(0, 8);
    }
    public User(String userId, String email, String username, String password, String phoneNumber, String role,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }  
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; } 
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }   
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // to string
    @Override
    public String toString() {
        return "User [userId=" + userId + ", email=" + email + ", username=" + username + ", password=" + password
                + ", phoneNumber=" + phoneNumber + ", role=" + role + ", createdAt=" + createdAt + ", updatedAt="
                + updatedAt + "]";
    }
    
}
