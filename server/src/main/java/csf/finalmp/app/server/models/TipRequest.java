package csf.finalmp.app.server.models;

// PURPOSE OF THIS MODEL
// RECEIVES AND PARSES TIP REQUESTS FROM CLIENT

public class TipRequest {

    // variables
    private Long musicianId;
    private Double amount;
    private String stripeToken;

    // constructors
    public TipRequest() {}
    public TipRequest(Long musicianId, Double amount, String stripeToken) {
        this.musicianId = musicianId;
        this.amount = amount;
        this.stripeToken = stripeToken;
    }

    // getters and setters
    public Long getMusicianId() { return musicianId; }
    public void setMusicianId(Long musicianId) { this.musicianId = musicianId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStripeToken() { return stripeToken; }
    public void setStripeToken(String stripeToken) { this.stripeToken = stripeToken; }
    
    // to string
    @Override
    public String toString() {
        return "TipRequest [musicianId=" + musicianId + ", amount=" + amount + ", stripeToken=" + stripeToken + "]";
    }   
    
}
