package csf.finalmp.app.server.models;

import java.time.LocalDateTime;
import java.util.Arrays;

// PURPOSE OF THIS MODEL
// STORE AND RETRIEVE BASIC INFO OF MUSICIANS

public class MusicianProfile {
    
    // variables
    private Long id;
    private Long userId;
    private String displayName;
    private String bio;
    private byte[] photo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors
    public MusicianProfile() {}
    public MusicianProfile(Long id, Long userId, String displayName, 
        String bio, byte[] photo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.displayName = displayName;
        this.bio = bio;
        this.photo = photo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "MusicianProfile [id=" + id + ", userId=" + userId + ", displayName=" + displayName + ", bio=" + bio
                + ", photo=" + Arrays.toString(photo) + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }    

}
