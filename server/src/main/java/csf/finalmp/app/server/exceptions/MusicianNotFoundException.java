package csf.finalmp.app.server.exceptions;

// PURPOSE OF THIS EXCEPTION CLASS
// CUSTOM MUSICIAN NOT FOUND EXCEPTION

public class MusicianNotFoundException extends RuntimeException {
    
    public MusicianNotFoundException(String message) {
        super(message);
    }

}
