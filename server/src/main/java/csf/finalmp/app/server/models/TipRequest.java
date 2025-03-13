package csf.finalmp.app.server.models;

// PURPOSE OF THIS MODEL
// RECEIVES AND PARSES TIP REQUESTS FROM CLIENT

public class TipRequest {

    // variables
    private String tipperId;
    private String artisteId;
    private Double amount;
    private String stripeToken;

    // constructors
    public TipRequest() {}
    public TipRequest(String tipperId, String artisteId, Double amount, String stripeToken) {
        this.tipperId = tipperId;
        this.artisteId = artisteId;
        this.amount = amount;
        this.stripeToken = stripeToken;
    }

    // getters and setters
    public String getTipperId() { return tipperId; }
    public void setTipperId(String tipperId) { this.tipperId = tipperId; } 
    public String getArtisteId() { return artisteId; }
    public void setArtisteId(String artisteId) { this.artisteId = artisteId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStripeToken() { return stripeToken; }
    public void setStripeToken(String stripeToken) { this.stripeToken = stripeToken; }
    
    @Override
    public String toString() {
        return "TipRequest [tipperId=" + tipperId + ", artisteId=" + artisteId + ", amount=" + amount
                + ", stripeToken=" + stripeToken + "]";
    }  
    
}
