package csf.finalmp.app.server.models;

// PURPOSE OF THIS MODEL
// RECEIVES AND PARSES TIP REQUESTS FROM CLIENT

public class TipRequest {

    // variables
    private Long tipperId;
    private Long musicianId;
    private Double amount;
    private String stripeToken;

    // constructors
    public TipRequest() {}
    public TipRequest(Long tipperId, Long musicianId, Double amount, String stripeToken) {
        this.tipperId = tipperId;
        this.musicianId = musicianId;
        this.amount = amount;
        this.stripeToken = stripeToken;
    }

    // getters and setters
    public Long getTipperId() { return tipperId; }
    public void setTipperId(Long tipperId) { this.tipperId = tipperId; } 
    public Long getMusicianId() { return musicianId; }
    public void setMusicianId(Long musicianId) { this.musicianId = musicianId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStripeToken() { return stripeToken; }
    public void setStripeToken(String stripeToken) { this.stripeToken = stripeToken; }
    
    @Override
    public String toString() {
        return "TipRequest [tipperId=" + tipperId + ", musicianId=" + musicianId + ", amount=" + amount
                + ", stripeToken=" + stripeToken + "]";
    }  
    
}
