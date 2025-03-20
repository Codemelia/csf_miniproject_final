package csf.finalmp.app.server.models;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

// ARTISTE PROFILE DETAILS

@Repository
public class ArtisteProfile {

    // saved on mongodb
    private String artisteId;
    private String stageName;
    private List<String> categories;
    private String bio;
    private byte[] photo;
    private byte[] qrCode;
    private String qrCodeUrl;
    private String thankYouMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors
    public ArtisteProfile() {}
    public ArtisteProfile(String artisteId, String stageName, List<String> categories, String bio, 
        byte[] photo, byte[] qrCode, String qrCodeUrl, String thankYouMessage, 
        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.artisteId = artisteId;
        this.stageName = stageName;
        this.categories = categories;
        this.bio = bio;
        this.photo = photo;
        this.qrCode = qrCode;
        this.qrCodeUrl = qrCodeUrl;
        this.thankYouMessage = thankYouMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getters and setters
    public String getArtisteId() { return artisteId; }
    public void setArtisteId(String artisteId) { this.artisteId = artisteId; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }
    public byte[] getQrCode() { return qrCode; }
    public void setQrCode(byte[] qrCode) { this.qrCode = qrCode; }
    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
    public String getThankYouMessage() { return thankYouMessage; }
    public void setThankYouMessage(String thankYouMessage) { this.thankYouMessage = thankYouMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    // to string
    @Override
    public String toString() {
        return "ArtisteProfile [artisteId=" + artisteId + ", stageName=" + stageName + ", categories="
                + categories.toString() + ", bio=" + bio + ", photo=" + Arrays.toString(photo) + ", qrCode="
                + Arrays.toString(qrCode) + ", qrCodeUrl=" + qrCodeUrl + ", thankYouMessage=" + thankYouMessage
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }
    
}
