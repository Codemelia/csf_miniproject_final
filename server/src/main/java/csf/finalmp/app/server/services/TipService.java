package csf.finalmp.app.server.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

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
    private ArtisteTransactionService artisteTransSvc;

    @Autowired
    private ArtisteProfileService artisteProfSvc;

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

        // check if artiste stage name exists in artistes table
        String artisteStageName = unconfirmedRequest.getStageName();
        boolean stageNameExists = artisteProfSvc.checkArtisteStageName(artisteStageName);

        // if artiste exists, proceed with trans
        // otherwise, throw error
        if (!stageNameExists) {
            logger.severe(
                ">>> Failed to process tip as Artiste with STAGE NAME %s could not be found".formatted(artisteStageName));
            throw new UserNotFoundException(
                "Vibee does not exist. Please ensure you have the correct Vibee Stage Name.");
        }
 
        // retrieve artiste id
        String artisteId = artisteProfSvc.getArtisteIdByStageName(artisteStageName);

        // check if artiste has stripe access token (i.e. registered stripe connected acc)
        boolean hasAccessToken = artisteTransSvc.checkArtisteStripeAccess(artisteId);

        if (!hasAccessToken) {
            throw new StripePaymentException("Vibee has not completed their payment profile. Please try again later.");
        }

        // get artiste stripe account id
        String artisteStripeId = artisteTransSvc.getStripeAccountId(artisteId);

        // get tipper id or assign new guest id if null
        String tipperId = unconfirmedRequest.getTipperId();
        if (tipperId == null || tipperId.isBlank()) {
            tipperId = String.format("G%s", UUID.randomUUID().toString().substring(0, 7));
        } 

        // get tip amount in cents
        long tipAmountInCents = Math.round(unconfirmedRequest.getAmount() * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(tipAmountInCents) // amount in cents
            .setCurrency("sgd") // standardise to sgd
            .setPaymentMethod(unconfirmedRequest.getPaymentMethodId()) // payment method (card)
            .setDescription("Tip for Artiste ID: " + artisteId) // description with musician's ID
            .putAllMetadata(Map.of("artisteId", artisteId, "tipperId", tipperId))
            .setTransferData(
                PaymentIntentCreateParams.TransferData.builder()
                    .setDestination(artisteStripeId) // Set destination to the musician's Stripe account
                    .build()
            )
            .setApplicationFeeAmount((long) (tipAmountInCents * 0.15)) // platform fee: 15%
            .build();

        // create payment intent with params and return client secret for client side confirmation
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // new map to return response to client
        Map<String, String> response = new HashMap<>();
        response.put("tipperId", tipperId); // sent to client bc it is needed if tipper is a guest to save on backend later
        response.put("clientSecret", paymentIntent.getClientSecret());

        return response;
 
    }

    // save tip details after stripe payment is confirmed
    @Transactional(rollbackFor = Exception.class)
    public String saveTip(Tip confirmedRequest) {

        // if tipper id is null, throw exception
        String tipperId = confirmedRequest.getTipperId();
        if (tipperId == null) {
            logger.severe(">>> Failed to save tip as Tipper ID is null".formatted(tipperId));
            throw new StripePaymentException("Payment failed. Please try again.");
        }

        // retrieve artiste id
        String artisteId = artisteProfSvc.getArtisteIdByStageName(confirmedRequest.getStageName());

        // set current tip variables
        Tip tip = new Tip();

        String tipperName = confirmedRequest.getTipperName();
        if (tipperName != null && !tipperName.isBlank()) { tip.setTipperName(tipperName); }
            else { tip.setTipperName("Viber"); }

        String tipperMessage = confirmedRequest.getTipperMessage();
        if (tipperMessage != null && !tipperMessage.isBlank()) { tip.setTipperMessage(tipperMessage); }
        else { tip.setTipperMessage("No message written"); }

        String tipperEmail = confirmedRequest.getTipperEmail();
        if (tipperEmail != null && !tipperEmail.isBlank()) tip.setTipperEmail(tipperEmail);

        tip.setTipperId(tipperId);
        tip.setArtisteId(artisteId);
        tip.setAmount(confirmedRequest.getAmount());
        tip.setPaymentIntentId(confirmedRequest.getPaymentIntentId());
        tip.setPaymentStatus(confirmedRequest.getPaymentStatus());

        return tipRepo.saveTip(tip); // returns artisteId

    }

    // get tips for artiste by fk artiste id
    public List<Tip> getTipsByArtisteId(String artisteId) {
        return tipRepo.getTipsByArtisteId(artisteId);
    }

}
