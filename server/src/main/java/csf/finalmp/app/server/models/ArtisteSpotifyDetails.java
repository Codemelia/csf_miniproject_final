package csf.finalmp.app.server.models;

import java.time.LocalDateTime;

public class ArtisteSpotifyDetails {

    // variables
    private String artisteId; // _id
    private String spotifyUserId;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessExpiresAt;

    private String playlistName;
    private String playlistUrl;
    private String playlistId;

    // constructors
    public ArtisteSpotifyDetails() {}
    public ArtisteSpotifyDetails(String artisteId, String spotifyUserId, String accessToken, String refreshToken,
            LocalDateTime accessExpiresAt, String playlistName, String playlistUrl, String playlistId) {
        this.artisteId = artisteId;
        this.spotifyUserId = spotifyUserId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessExpiresAt = accessExpiresAt;
        this.playlistName = playlistName;
        this.playlistUrl = playlistUrl;
        this.playlistId = playlistId;
    }

    // getters and setters
    public String getArtisteId() { return artisteId; }
    public void setArtisteId(String artisteId) { this.artisteId = artisteId; }
    public String getSpotifyUserId() { return spotifyUserId; }
    public void setSpotifyUserId(String spotifyUserId) { this.spotifyUserId = spotifyUserId; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public LocalDateTime getAccessExpiresAt() { return accessExpiresAt; }
    public void setAccessExpiresAt(LocalDateTime accessExpiresAt) { this.accessExpiresAt = accessExpiresAt; }

    public String getPlaylistName() { return playlistName; }
    public void setPlaylistName(String playlistName) { this.playlistName = playlistName; }
    public String getPlaylistUrl() { return playlistUrl; }
    public void setPlaylistUrl(String playlistUrl) { this.playlistUrl = playlistUrl; }
    public String getPlaylistId() { return playlistId; }
    public void setPlaylistId(String playlistId) { this.playlistId = playlistId; }

    // to string
    @Override
    public String toString() {
        return "ArtisteSpotifyDetails [artisteId=" + artisteId + ", spotifyUserId=" + spotifyUserId + ", accessToken="
                + accessToken + ", refreshToken=" + refreshToken + ", accessExpiresAt=" + accessExpiresAt
                + ", playlistName=" + playlistName + ", playlistUrl=" + playlistUrl + ", playlistId=" + playlistId
                + "]";
    }

}
