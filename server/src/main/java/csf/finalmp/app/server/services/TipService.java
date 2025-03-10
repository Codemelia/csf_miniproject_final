package csf.finalmp.app.server.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import csf.finalmp.app.server.exceptions.custom.MusicianNotFoundException;
import csf.finalmp.app.server.exceptions.custom.StripePaymentException;
import csf.finalmp.app.server.models.Tip;
import csf.finalmp.app.server.models.TipRequest;
import csf.finalmp.app.server.repositories.TipRepository;
import jakarta.annotation.PostConstruct;

// FOR TIPPING FUNCTIONS

@Service
public class TipService {

    // stripe secret key
    @Value("${stripe.secret.key}")
    private String stripeKey;

    @Autowired
    private TipRepository tipRepo;

    // for inter-table validation only
    @Autowired
    private MusicianProfileService musicSvc;

    // initialise stripe api key
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeKey;
    }

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

    // insert tip if musician exists, if not throw error
    public Map<String, Object> processTip(TipRequest request) throws StripeException {

        // get request details
        Long tipperId = request.getTipperId();
        Long musicianId = request.getMusicianId();
        Double amount = request.getAmount();

        // check if musician id exists in musicians table
        boolean musicianExists = musicSvc.checkMusicianId(musicianId);

        // if musician exists, proceed with trans
        // otherwise, throw error
        if (musicianExists) {

            // handle invalid stripe params
            // avoid unnecessary api calls
            /*
                if (stripeToken == null || stripeToken.isEmpty()) {
                    logger.severe(">>> Stripe param STRINGTOKEN is invalid");
                    throw new StripeParamException(
                        "Stripe transaction failed. Please ensure your card details are correct.");
                }
            */

            if (amount == null || amount <= 0) {
                logger.severe(">>> Stripe param AMOUNT is invalid");
                throw new StripePaymentException(
                    "Stripe transaction failed. Please ensure your card details are correct.");
            }

            // set charge params
            Map<String, Object> params = new HashMap<>();
            params.put("amount", amount * 100); // convert amt to cents
            params.put("currency", "sgd"); // standardise all to sgd
            params.put("payment_method_types", List.of("card")); // declare payment method
            params.put("description", "Tip for Musician ID: %d".formatted(musicianId)); // musician id for id
            params.put("metadata", Map.of("musicianId", request.getMusicianId(), "tipperId", request.getTipperId()));

            // create payment intent with set params
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // set current tip variables
            Tip tip = new Tip();
            tip.setTipperId(tipperId);
            tip.setMusicianId(musicianId);
            tip.setAmount(amount);
            tip.setPaymentIntentId(paymentIntent.getId());
            tip.setPaymentStatus(paymentIntent.getStatus());

            // insert tip to db
            Long id = tipRepo.saveTip(tip);
            tip.setId(id); // set new id to tip object

            // put client secret and tip in map and return
            Map<String, Object> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            response.put("tip", tip);

            return response;

        } else {
            logger.severe(
                ">>> Failed to process tip as musician with ID %d could not be found".formatted(request.getMusicianId()));
            throw new MusicianNotFoundException(
                "Vibee does not exist. Please ensure you have the correct Vibee ID.");
        }
 
    }

    // update tipe from stripe webhook
    public void updateTip(String paymentIntentId, String paymentStatus) {

        Tip tip = tipRepo.getTipByPid(paymentIntentId);

        // if tip exists, update payment status and save
        if (tip != null) {
            tip.setPaymentStatus(paymentStatus);
            tipRepo.saveTip(tip);
        } else {
            logger.severe("Tip not found for PaymentIntent ID: " + paymentIntentId);
            throw new StripePaymentException(
                "Payment failed. Please try again.");
        }

    }

    // get tips for musician by fk musician id
    public List<Tip> getTipsByMusicianId(Long musicianId) {
        return tipRepo.getTipsByMusicianId(musicianId);
    }

}
