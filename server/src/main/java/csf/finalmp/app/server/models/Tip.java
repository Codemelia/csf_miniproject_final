package csf.finalmp.app.server.models;

import java.time.LocalDateTime;

// PURPOSE OF THIS MODEL
// STORE AND RETRIEVE TIP DETAILS

public class Tip {
    
    // variables
    private Long id;
    private Long tipperId;
    private Long musicianId;
    private Double amount;
    private String stripeChargeId;
    private String transactionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors
    public Tip() {}
    public Tip(Long id, Long tipperId, Long musicianId, Double amount, String stripeChargeId, 
        String transactionStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tipperId = tipperId;
        this.musicianId = musicianId;
        this.amount = amount;
        this.stripeChargeId = stripeChargeId;
        this.transactionStatus = transactionStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTipperId() { return tipperId; }
    public void setTipperId(Long tipperId) { this.tipperId = tipperId; }
    public Long getMusicianId() { return musicianId; }
    public void setMusicianId(Long musicianId) { this.musicianId = musicianId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStripeChargeId() { return stripeChargeId; }
    public void setStripeChargeId(String stripeChargeId) { this.stripeChargeId = stripeChargeId; }
    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // to string
    @Override
    public String toString() {
        return "Tip [id=" + id + ", tipperId=" + tipperId + ", musicianId=" + musicianId + ", amount=" + amount
                + ", stripeChargeId=" + stripeChargeId + ", transactionStatus=" + transactionStatus + ", createdAt="
                + createdAt + ", updatedAt=" + updatedAt + "]";
    }

}
