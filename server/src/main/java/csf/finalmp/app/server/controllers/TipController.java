package csf.finalmp.app.server.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;

import csf.finalmp.app.server.exceptions.custom.StripePaymentException;
import csf.finalmp.app.server.models.Tip;
import csf.finalmp.app.server.services.TipService;

// PURPOSE OF THIS CONTROLLER
// PROVIDE REST ENDPOINTS FOR TIP REQUESTS FROM CLIENT

@CrossOrigin(originPatterns = "http://localhost:4200", allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/tips", produces = MediaType.APPLICATION_JSON_VALUE)
public class TipController {

    @Autowired
    private TipService tipSvc;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(TipController.class.getName());

    // create payment intent and return client secret
    @PostMapping(path="/process", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPaymentIntentClientSecret(
        @RequestBody Tip unconfirmedRequest) throws StripeException { // throw exception for global handler

        logger.info(">>> Processing tip request: %s".formatted(unconfirmedRequest.toString()));
        String clientSecret = tipSvc.getPaymentIntentClientSecret(unconfirmedRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientSecret);

    }

    // update payment status
    // if payment status successful, add tip to wallet
    @PostMapping("/save")
    public ResponseEntity<Long> saveTip(
        @RequestBody Tip confirmedRequest) {

        // payment intent id and status
        String paymentIntentId = confirmedRequest.getPaymentIntentId();
        String paymentStatus = confirmedRequest.getPaymentStatus();

        logger.info(">>> Processing tip confirmation for Payment Intent: %s".formatted(paymentIntentId));
        if (paymentStatus.contains("succeeded")) {
            Long tipId = tipSvc.saveTip(confirmedRequest);
            logger.info(">>> Tip saved with ID: %d".formatted(tipId));
            return ResponseEntity.ok().body(tipId);
        } else {
            throw new StripePaymentException("Payment failed. Please try again."); // throw exception as I am only saving successful payments
        }
    
    }

    // get tip by artiste id
    // returns tip object
    @GetMapping(path = "/{artisteId}")
    public ResponseEntity<List<Tip>> getTipsByArtisteId(
        @PathVariable String artisteId
    ) {
        
        logger.info(">>> Fetching tips for artiste with ID: %s".formatted(artisteId));
        List<Tip> tips = tipSvc.getTipsByArtisteId(artisteId);
        return ResponseEntity.ok().body(tips);

    }


}
