package csf.finalmp.app.server.exceptions.custom;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
}
