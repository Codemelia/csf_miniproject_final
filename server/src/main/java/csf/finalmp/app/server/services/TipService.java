package csf.finalmp.app.server.services;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import csf.finalmp.app.server.exceptions.MusicianNotFoundException;
import csf.finalmp.app.server.exceptions.StripeParamException;
import csf.finalmp.app.server.models.Tip;
import csf.finalmp.app.server.models.TipRequest;
import csf.finalmp.app.server.repositories.TipRepository;
import jakarta.annotation.PostConstruct;

@Service
public class TipService {

    // stripe secret key
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Autowired
    private TipRepository tipRepo;

    // for inter-table validation only
    @Autowired
    private MusicianService musicSvc;

    // initialise service with stripe api key
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
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
    public Tip insertTip(TipRequest request) throws StripeException {

        // get request details
        Long musicianId = request.getMusicianId();
        Double amount = request.getAmount();
        String stripeToken = request.getStripeToken();

        // check if musician id exists in musicians table
        boolean musicianExists = musicSvc.checkMusicianId(musicianId);

        // if musician exists, proceed with trans
        // otherwise, throw error
        if (musicianExists) {

            // handle invalid stripe params
            // avoid unnecessary api calls
            if (stripeToken == null || stripeToken.isEmpty()) {
                throw new StripeParamException(
                    "Stripe param STRINGTOKEN is invalid: %s".formatted(stripeToken)
                );
            }

            if (amount == null || amount <= 0) {
                throw new StripeParamException(
                    "Stripe param AMOUNT is invalid: %d".formatted(amount)
                );
            }

            // set charge params
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", amount * 100); // convert amt to cents
            chargeParams.put("currency", "sgd"); // standardise all to sgd
            chargeParams.put("source", stripeToken); // token from frontend
            chargeParams.put("description", "Tip for Musician ID: %d".formatted(musicianId)); // musician id for id

            // create charge with set params
            Charge charge = Charge.create(chargeParams);

            // set current tip variables
            Tip tip = new Tip();
            tip.setMusicianId(musicianId);
            tip.setAmount(amount);
            tip.setStripeChargeId(charge.getId());

            // insert tip to db
            Long id = tipRepo.insertTip(tip);
            tip.setId(id); // set new id to tip object
            return tip; // return updated tip object

        } else {
            throw new MusicianNotFoundException(
                "Musician with ID %d could not be found for Stripe payment".formatted(musicianId));
        }
 
    }

}
