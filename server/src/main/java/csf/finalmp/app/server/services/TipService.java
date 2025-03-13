package csf.finalmp.app.server.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.exceptions.custom.StripePaymentException;
import csf.finalmp.app.server.models.Tip;
import csf.finalmp.app.server.models.TipRequest;
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
    public Map<String, Object> processTip(TipRequest request) throws StripeException {

        // get request details
        String tipperId = request.getTipperId();
        String artisteId = request.getArtisteId();
        Double amount = request.getAmount();

        // check if artiste id exists in artistes table
        boolean artisteExists = artisteSvc.checkArtisteId(artisteId);

        // if artiste exists, proceed with trans
        // otherwise, throw error
        if (artisteExists) {

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
            params.put("description", "Tip for Artiste ID: %s".formatted(artisteId)); // artiste id for id
            params.put("metadata", Map.of("artisteId", request.getArtisteId(), "tipperId", request.getTipperId()));

            // create payment intent with set params
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // set current tip variables
            Tip tip = new Tip();
            tip.setTipperId(tipperId);
            tip.setArtisteId(artisteId);
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
                ">>> Failed to process tip as artisteId with ID %s could not be found".formatted(request.getArtisteId()));
            throw new UserNotFoundException(
                "Vibee does not exist. Please ensure you have the correct Vibee ID.");
        }
 
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
