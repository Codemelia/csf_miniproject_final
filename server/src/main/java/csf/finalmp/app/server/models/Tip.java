package csf.finalmp.app.server.models;

// PURPOSE OF THIS MODEL
// STORE AND RETRIEVE TIP DETAILS

public class Tip {
    
    // variables
    private Long id;
    private Long musicianId;
    private Double amount;
    private String stripeChargeId;

    // constructors
    public Tip() {}
    public Tip(Long id, Double amount, String stripeChargeId, Long musicianId) {
        this.id = id;
        this.musicianId = musicianId;
        this.amount = amount;
        this.stripeChargeId = stripeChargeId;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMusicianId() { return musicianId; }
    public void setMusicianId(Long musicianId) { this.musicianId = musicianId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStripeChargeId() { return stripeChargeId; }
    public void setStripeChargeId(String stripeChargeId) { this.stripeChargeId = stripeChargeId; }

    // to string
    @Override
    public String toString() {
        return "Tip [id=" + id + ", musicianId=" + musicianId + ", amount=" + amount + ", stripeChargeId="
                + stripeChargeId + "]";
    }   

}
