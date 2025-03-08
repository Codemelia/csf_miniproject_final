package csf.finalmp.app.server.models.authentication;

// PURPOSE OF THIS MODEL
// SEND AUTH RESPONSE AFTER VALIDATING AUTH REQUESTS

public class AuthResponse {

    // variables
    private Long id; // for registration
    private String token; // for login
    private String message;

    // constructors
    public AuthResponse() {}
    public AuthResponse(Long id, String token, String message) {
        this.id = id;
        this.token = token;
        this.message = message;
    }

    // customised constructors for cleaner code
    public static AuthResponse forRegistration(Long id, String message) {
        return new AuthResponse(id, null, message);
    }
    public static AuthResponse forLogin(String token, String message) {
        return new AuthResponse(null, token, message);
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // to string
    @Override
    public String toString() {
        return "AuthResponse [token=" + token + ", message=" + message + "]";
    }
    
}
