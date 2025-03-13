package csf.finalmp.app.server.models;

public class StripeTokens {
    
    // variables
    private String artisteId; // same as user id
    private String stripeAccessToken;
    private String stripeRefreshToken;

    // constructors
    public StripeTokens() {};
    public StripeTokens(String artisteId, String stripeAccessToken, String stripeRefreshToken) {
        this.artisteId = artisteId;
        this.stripeAccessToken = stripeAccessToken;
        this.stripeRefreshToken = stripeRefreshToken;
    }

    // getters and setters
    public String getArtisteId() { return artisteId; }
    public void setArtisteId(String artisteId) { this.artisteId = artisteId; }
    public String getStripeAccessToken() { return stripeAccessToken; }
    public void setStripeAccessToken(String stripeAccessToken) { this.stripeAccessToken = stripeAccessToken; }
    public String getStripeRefreshToken() { return stripeRefreshToken; }
    public void setStripeRefreshToken(String stripeRefreshToken) { this.stripeRefreshToken = stripeRefreshToken; }
    
    // to string
    @Override
    public String toString() {
        return "ArtisteStripeTokens [artisteId=" + artisteId + ", stripeAccessToken=" + stripeAccessToken
                + ", stripeRefreshToken=" + stripeRefreshToken + "]";
    }

}
