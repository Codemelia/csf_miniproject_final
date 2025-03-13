package csf.finalmp.app.server.models;

import java.time.LocalDateTime;
import java.util.Arrays;

// STORE AND RETRIEVE ARTISTE DETAILS

public class Artiste {
    
    // variables
    private String artisteId; // same as user id
    private String stageName;
    private String bio;
    private byte[] photo;
    private byte[] qrCode;
    private String qrCodeUrl;
    private String stripeAccountId;
    private String stripeAccessToken;
    private String stripeRefreshToken;
    private Double walletBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors
    public Artiste() {}
    // for full mysql retrieval
    public Artiste(String artisteId, String stageName, String bio, byte[] photo, byte[] qrCode, String qrCodeUrl, 
        String stripeAccountId, String stripeAccessToken, String stripeRefreshToken, Double walletBalance, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.artisteId = artisteId;
        this.stageName = stageName;
        this.bio = bio;
        this.photo = photo;
        this.qrCode = qrCode;
        this.qrCodeUrl = qrCodeUrl;
        this.stripeAccountId = stripeAccountId;
        this.stripeAccessToken = stripeAccessToken;
        this.stripeRefreshToken = stripeRefreshToken;
        this.walletBalance = walletBalance;
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
    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
    public String getStripeAccountId() { return stripeAccountId; }
    public void setStripeAccountId(String stripeAccountId) { this.stripeAccountId = stripeAccountId; }
    public String getStripeAccessToken() { return stripeAccessToken; }
    public void setStripeAccessToken(String stripeAccessToken) { this.stripeAccessToken = stripeAccessToken; }
    public String getStripeRefreshToken() { return stripeRefreshToken; }
    public void setStripeRefreshToken(String stripeRefreshToken) { this.stripeRefreshToken = stripeRefreshToken; }
    public Double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(Double walletBalance) { this.walletBalance = walletBalance; }    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public byte[] getQrCode() { return qrCode; }
    public void setQrCode(byte[] qrCode) { this.qrCode = qrCode; }   

    // to string
    @Override
    public String toString() {
        return "Artiste [artisteId=" + artisteId + ", stageName=" + stageName + ", bio=" + bio + ", photo="
                + Arrays.toString(photo) + ", qrCode=" + Arrays.toString(qrCode) + ", qrCodeUrl=" + qrCodeUrl
                + ", stripeAccountId=" + stripeAccountId + ", stripeAccessToken=" + stripeAccessToken
                + ", stripeRefreshToken=" + stripeRefreshToken + ", walletBalance=" + walletBalance + ", createdAt="
                + createdAt + ", updatedAt=" + updatedAt + "]";
    }

}
