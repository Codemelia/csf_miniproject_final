package csf.finalmp.app.server.models;

import java.time.LocalDateTime;

// PURPOSE OF THIS MODEL
// STORE AND RETRIEVE TIP DETAILS

public class Tip {
    
    // variables
    private Long tipId;
    private String tipperId;
    private String artisteId;
    private Double amount;
    private String paymentIntentId;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors
    public Tip() {}
    public Tip(Long tipId, String tipperId, String artisteId, Double amount, String paymentIntentId, 
        String paymentStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tipId = tipId;
        this.tipperId = tipperId;
        this.artisteId = artisteId;
        this.amount = amount;
        this.paymentIntentId = paymentIntentId;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // getters and setters
    public Long getId() { return tipId; }
    public void setId(Long tipId) { this.tipId = tipId; }
    public String getTipperId() { return tipperId; }
    public void setTipperId(String tipperId) { this.tipperId = tipperId; }
    public String getArtisteId() { return artisteId; }
    public void setArtisteId(String artisteId) { this.artisteId = artisteId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // to string
    @Override
    public String toString() {
        return "Tip [tipId=" + tipId + ", tipperId=" + tipperId + ", artisteId=" + artisteId + ", amount=" + amount
                + ", paymentIntentId=" + paymentIntentId + ", paymentStatus=" + paymentStatus + ", createdAt="
                + createdAt + ", updatedAt=" + updatedAt + "]";
    }

}
