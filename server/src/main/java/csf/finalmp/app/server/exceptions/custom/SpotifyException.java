package csf.finalmp.app.server.exceptions.custom;

// CUSTOM SPOTIFY EXCEPTION

public class SpotifyException extends RuntimeException {
    
    public SpotifyException(String message) {
        super(message);
    }

}
