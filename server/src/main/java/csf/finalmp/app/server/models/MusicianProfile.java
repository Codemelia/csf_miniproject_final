package csf.finalmp.app.server.models;

import java.time.LocalDateTime;
import java.util.Arrays;

// PURPOSE OF THIS MODEL
// STORE AND RETRIEVE BASIC INFO OF MUSICIANS

public class MusicianProfile {
    
    // variables
    private Long userId; // same as user id
    private String stageName;
    private String bio;
    private byte[] photo;
    private String qrCodeUrl;
    private String stripeAccountId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors
    public MusicianProfile() {}
    public MusicianProfile(Long userId, String stageName, String bio, byte[] photo, String qrCodeUrl,
            String stripeAccountId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.stageName = stageName;
        this.bio = bio;
        this.photo = photo;
        this.qrCodeUrl = qrCodeUrl;
        this.stripeAccountId = stripeAccountId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }
    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
    public String getStripeAccountId() { return stripeAccountId; }
    public void setStripeAccountId(String stripeAccountId) { this.stripeAccountId = stripeAccountId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // to string
    public String toString() {
        return "MusicianProfile [userId=" + userId + ", stageName=" + stageName + ", bio=" + bio + ", photo="
                + Arrays.toString(photo) + ", qrCodeUrl=" + qrCodeUrl + ", stripeAccountId=" + stripeAccountId
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }    

}
