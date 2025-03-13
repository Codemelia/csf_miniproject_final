package csf.finalmp.app.server.models.unused;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Wallet {
    
    // variables
    private Long artisteId;
    private BigDecimal balance = BigDecimal.ZERO;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // getters and setters
    public Long getArtisteId() { return artisteId; }
    public void setArtisteId(Long artisteId) { this.artisteId = artisteId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // constructors
    public Wallet() {}
    public Wallet(Long id, Long artisteId, BigDecimal balance, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.artisteId = artisteId;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Wallet [artisteId=" + artisteId + ", balance=" + balance + ", createdAt=" + createdAt + ", updatedAt="
                + updatedAt + "]";
    }    

}
