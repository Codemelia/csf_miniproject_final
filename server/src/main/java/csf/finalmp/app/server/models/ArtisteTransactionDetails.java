package csf.finalmp.app.server.models;

import java.time.LocalDateTime;

// STORE AND RETRIEVE ARTISTE DETAILS PRIMARILY FOR TRANSACTIONS

public class ArtisteTransactionDetails {
    
    // saved on mysql
    private String artisteId; // same as user id
    private String stripeAccountId;
    private String stripeAccessToken;
    private String stripeRefreshToken;
    private Double walletBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors
    public ArtisteTransactionDetails() {}
    // for full mysql retrieval
    public ArtisteTransactionDetails(String artisteId,  String stripeAccountId, String stripeAccessToken, String stripeRefreshToken, 
        Double walletBalance, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.artisteId = artisteId;
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

    // to string
    @Override
    public String toString() {
        return "ArtisteTransaction [artisteId=" + artisteId + ", stripeAccountId=" + stripeAccountId
                + ", stripeAccessToken=" + stripeAccessToken + ", stripeRefreshToken=" + stripeRefreshToken
                + ", walletBalance=" + walletBalance + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }

}
