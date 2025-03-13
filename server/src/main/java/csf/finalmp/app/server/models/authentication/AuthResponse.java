package csf.finalmp.app.server.models.authentication;

// PURPOSE OF THIS MODEL
// SEND AUTH RESPONSE AFTER VALIDATING AUTH REQUESTS

public class AuthResponse {

    // variables
    private String userId; // for registration
    private String token; // for login
    private String message;

    // constructors
    public AuthResponse() {}
    public AuthResponse(String userId, String token, String message) {
        this.userId = userId;
        this.token = token;
        this.message = message;
    }

    // customised constructors for cleaner code
    public static AuthResponse forRegistration(String userId, String message) {
        return new AuthResponse(userId, null, message);
    }
    public static AuthResponse forLogin(String token, String message) {
        return new AuthResponse(null, token, message);
    }

    // getters and setters
    public String getUserId() { return userId; }
    public void getUserId(String userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // to string
    @Override
    public String toString() {
        return "AuthResponse [userId=" + userId + ", token=" + token + ", message=" + message + "]";
    }
    
}
