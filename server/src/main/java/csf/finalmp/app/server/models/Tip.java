package csf.finalmp.app.server.models;

import java.time.LocalDateTime;

import com.stripe.model.PaymentMethod;

// PURPOSE OF THIS MODEL
// STORE AND RETRIEVE TIP DETAILS

public class Tip {
    
    // variables
    private Long tipId;
    private String tipperName;
    private String tipperMessage;
    private String tipperEmail; // for receipt email
    private String tipperId; // not fk as might be guest id
    private String artisteId;
    private Double amount;
    private String paymentIntentId;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // will not be saved in mysql
    private String stageName;
    private String paymentMethodId;

    // constructors
    public Tip() {}
    public Tip(Long tipId, String tipperName, String tipperMessage, String tipperEmail, String tipperId, String artisteId, Double amount, String paymentIntentId, 
        String paymentStatus, LocalDateTime createdAt, LocalDateTime updatedAt, String stageName, String paymentMethodId) {
        this.tipId = tipId;
        this.tipperName = tipperName;
        this.tipperMessage = tipperMessage;
        this.tipperEmail = tipperEmail;
        this.tipperId = tipperId;
        this.artisteId = artisteId;
        this.amount = amount;
        this.paymentIntentId = paymentIntentId;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stageName = stageName;
        this.paymentMethodId = paymentMethodId;
    }

    // getters and setters
    public Long getId() { return tipId; }
    public void setId(Long tipId) { this.tipId = tipId; }
    public String getTipperId() { return tipperId; }
    public void setTipperId(String tipperId) { this.tipperId = tipperId; }
    public String getTipperName() { return tipperName; }
    public void setTipperName(String tipperName) { this.tipperName = tipperName; }
    public String getTipperMessage() { return tipperMessage; }
    public void setTipperMessage(String tipperMessage) { this.tipperMessage = tipperMessage; }
    public String getTipperEmail() { return tipperEmail; }
    public void setTipperEmail(String tipperEmail) { this.tipperEmail = tipperEmail; }
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
    public Long getTipId() { return tipId; }
    public void setTipId(Long tipId) { this.tipId = tipId; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    
    // to string
    @Override
    public String toString() {
        return "Tip [tipId=" + tipId + ", tipperName=" + tipperName + ", tipperMessage=" + tipperMessage
                + ", tipperEmail=" + tipperEmail + ", tipperId=" + tipperId + ", artisteId=" + artisteId + ", amount="
                + amount + ", paymentIntentId=" + paymentIntentId + ", paymentStatus=" + paymentStatus + ", createdAt="
                + createdAt + ", updatedAt=" + updatedAt + ", stageName=" + stageName + ", paymentMethodId="
                + paymentMethodId + "]";
    }

}
