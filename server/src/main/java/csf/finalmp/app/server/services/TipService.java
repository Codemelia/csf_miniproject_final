package csf.finalmp.app.server.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.exceptions.custom.StripePaymentException;
import csf.finalmp.app.server.models.Tip;
import csf.finalmp.app.server.repositories.TipRepository;

// FOR TIPPING FUNCTIONS

@Service
public class TipService {

    @Autowired
    private TipRepository tipRepo;

    // for inter-table validation only
    @Autowired
    private ArtisteService artisteSvc;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(TipService.class.getName());

    // check if table exists by checking count
    public boolean tableExists() {

        try {
            Integer count = tipRepo.checkCount();
            logger.info(">>> MySQL: Tips table SIZE: %d".formatted(count));
            return count != null && count >= 0;
        } catch (DataAccessException e) {
            logger.info(">>> MySQL: Tips table does not exist");
            return false; // return false if data access exception occurs
        }

    }

    // create table
    public void createTable() {
        tipRepo.createTable();
        logger.info(">>> MySQL: Tips table created");
    }

    // insert tip if artiste exists, if not throw error
    public Map<String, String> getPaymentIntentClientSecret(Tip unconfirmedRequest) throws StripeException {

        String artisteStageName = unconfirmedRequest.getStageName();

        // if stage name null, throw error
        if (artisteStageName == null) {
            throw new IllegalArgumentException("Artiste stage name cannot be null.");
        }    

        // check if artiste stage name exists in artistes table
        boolean stageNameExists = artisteSvc.checkArtisteStageName(artisteStageName);

        // if artiste exists, proceed with trans
        // otherwise, throw error
        if (!stageNameExists) {
            logger.severe(
                ">>> Failed to process tip as Artiste with STAGE NAME %s could not be found".formatted(artisteStageName));
            throw new UserNotFoundException(
                "Vibee does not exist. Please ensure you have the correct Vibee Stage Name.");
        }
 
        // retrieve artiste id
        String artisteId = artisteSvc.getArtisteIdByStageName(artisteStageName);

        // check if artiste has stripe access token (i.e. registered stripe connected acc)
        boolean hasAccessToken = artisteSvc.checkArtisteStripeAccess(artisteId);

        if (!hasAccessToken) {
            throw new StripePaymentException("Vibee has not completed their payment profile. Please try again later.");
        }

        // get tipper id
        String tipperId = unconfirmedRequest.getTipperId();

        // if tipper id is null, assign a new guest id
        if (tipperId == null || tipperId.isBlank()) {
            tipperId = String.format("G%s", UUID.randomUUID().toString().substring(0, 7));
        } 

        Double amount = unconfirmedRequest.getAmount();

        if (amount == null || amount <= 0) {
            logger.severe(">>> Stripe param AMOUNT is invalid");
            throw new StripePaymentException(
                "Stripe transaction failed. Please ensure your card details are correct.");
        }

        // set charge params
        Map<String, Object> params = new HashMap<>();
        params.put("amount", Math.round(amount * 100)); // convert amt to cents
        params.put("currency", "sgd"); // standardise all to sgd
        params.put("payment_method_types", List.of("card")); // declare payment method
        params.put("description", "Tip for Artiste ID: %s".formatted(artisteId)); // artiste id for id
        params.put("metadata", Map.of("artisteId", artisteId, "tipperId", tipperId));

        // create payment intent with params and return client secret for client side confirmation
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // new map to return response to client
        Map<String, String> response = new HashMap<>();
        response.put("tipperId", tipperId); // sent to client bc it is needed if tipper is a guest to save on backend later
        response.put("clientSecret", paymentIntent.getClientSecret());

        return response;
 
    }

    // save tip details after stripe payment is confirmed
    public Long saveTip(Tip confirmedRequest) {

        // if tipper id is null, throw exception
        String tipperId = confirmedRequest.getTipperId();
        if (tipperId == null) {
            logger.severe(">>> Failed to save tip as Tipper ID is null".formatted(tipperId));
            throw new StripePaymentException("Payment failed. Please try again.");
        }

        // retrieve artiste id
        String artisteId = artisteSvc.getArtisteIdByStageName(confirmedRequest.getStageName());

        // set current tip variables
        Tip tip = new Tip();

        String tipperName = confirmedRequest.getTipperName();
        if (tipperName != null && !tipperName.isBlank()) tip.setTipperName(tipperName);

        String tipperMessage = confirmedRequest.getTipperMessage();
        if (tipperMessage != null && !tipperMessage.isBlank()) tip.setTipperMessage(tipperMessage);

        String tipperEmail = confirmedRequest.getTipperEmail();
        if (tipperEmail != null && !tipperEmail.isBlank()) tip.setTipperEmail(tipperEmail);

        tip.setTipperId(tipperId);
        tip.setArtisteId(artisteId);
        tip.setAmount(confirmedRequest.getAmount());
        tip.setPaymentIntentId(confirmedRequest.getPaymentIntentId());
        tip.setPaymentStatus(confirmedRequest.getPaymentStatus());

        return tipRepo.saveTip(tip); // returns Long tip id

    }

    // update tip from stripe
    public Tip updateTip(String paymentIntentId, String paymentStatus) {

        Tip tip = tipRepo.getTipByPid(paymentIntentId);

        // if tip exists, update payment status and save
        if (tip != null) {
            tip.setPaymentStatus(paymentStatus);
            tipRepo.saveTip(tip);
            return tip;
        } else {
            logger.severe("Tip not found for PaymentIntent ID: " + paymentIntentId);
            throw new StripePaymentException(
                "Payment failed. Please try again.");
        }

    }

    // add tip to artiste wallet after successful payent
    public void addTipToWallet(String artisteId, Double amount) {

        boolean artisteExists = artisteSvc.checkArtisteId(artisteId);
        if (!artisteExists) {
            throw new UserNotFoundException("Vibee not found. Please ensure you have the correct Vibee ID.");
        }
        Double balance = artisteSvc.getBalance(artisteId);
        balance += amount; // update balance

        // set update info to artiste and save in db
        artisteSvc.updateArtisteWallet(artisteId, balance);

    }

    // get tips for artiste by fk artiste id
    public List<Tip> getTipsByArtisteId(String artisteId) {
        return tipRepo.getTipsByArtisteId(artisteId);
    }

}
