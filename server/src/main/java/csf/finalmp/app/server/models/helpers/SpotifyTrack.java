package csf.finalmp.app.server.models.helpers;

// track result from spotify

public class SpotifyTrack {

    // variables
    private String trackId;
    private String embedUrl;

    // constructors
    public SpotifyTrack() {}
    public SpotifyTrack(String trackId, String embedUrl) {
        this.trackId = trackId;
        this.embedUrl = embedUrl;
    }

    // getters and setters
    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }
    public String getEmbedUrl() { return embedUrl; }
    public void setEmbedUrl(String embedUrl) { this.embedUrl = embedUrl; }

    // to string
    @Override
    public String toString() {
        return "SpotifyTrack [trackId=" + trackId + ", embedUrl=" + embedUrl + "]";
    }
    
}
