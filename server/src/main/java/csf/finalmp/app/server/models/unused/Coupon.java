package csf.finalmp.app.server.models.unused;

import java.time.LocalDateTime;

public class Coupon {

    // variables
    private Long id;
    private Long tipId;
    private String sponsorName;
    private String code;
    private String discount;
    private LocalDateTime expiresAt;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTipId() { return tipId; }
    public void setTipId(Long tipId) { this.tipId = tipId; }
    public String getSponsorName() { return sponsorName; }
    public void setSponsorName(String sponsorName) { this.sponsorName = sponsorName; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    // constructors
    public Coupon() {}
    public Coupon(Long id, Long tipId, String sponsorName, String code, String discount, LocalDateTime expiresAt) {
        this.id = id;
        this.tipId = tipId;
        this.sponsorName = sponsorName;
        this.code = code;
        this.discount = discount;
        this.expiresAt = expiresAt;
    }

    // to string
    @Override
    public String toString() {
        return "Coupon [id=" + id + ", tipId=" + tipId + ", sponsorName=" + sponsorName + ", code=" + code
                + ", discount=" + discount + ", expiresAt=" + expiresAt + "]";
    }
    
}
