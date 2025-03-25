package csf.finalmp.app.server.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;

import csf.finalmp.app.server.exceptions.custom.StripePaymentException;
import csf.finalmp.app.server.models.Tip;
import csf.finalmp.app.server.services.ArtisteProfileService;
import csf.finalmp.app.server.services.ArtisteTransactionService;
import csf.finalmp.app.server.services.EmailService;
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

    @Autowired
    private ArtisteTransactionService artisteTransSvc;

    @Autowired
    private ArtisteProfileService artisteProfSvc;

    @Autowired
    private EmailService emailSvc;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(TipController.class.getName());

    // create payment intent and return client secret
    @PostMapping(path="/process", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> getPaymentIntentClientSecret(
        @RequestBody Tip unconfirmedRequest) throws StripeException { // throw exception for global handler

        logger.info(">>> Processing tip request: %s".formatted(unconfirmedRequest.toString()));
        Map<String, String> response = tipSvc.getPaymentIntentClientSecret(unconfirmedRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    // update payment status
    // if payment status successful, add tip to wallet
    @PostMapping("/save")
    public ResponseEntity<String> saveTip(
        @RequestBody Tip confirmedRequest) throws IOException {

        logger.info(">>> Processing tip confirmation for Tip Request: %s".formatted(confirmedRequest.toString()));
        if (confirmedRequest.getPaymentStatus().contains("succeeded")) {
            String artisteId = tipSvc.saveTip(confirmedRequest);
            artisteTransSvc.updateArtisteEarnings(artisteId, confirmedRequest.getAmount());
            logger.info(">>> Tip saved for Artiste with ID: %s".formatted(artisteId));
            String thankYouMessage = artisteProfSvc.getArtisteThankYouMsgById(artisteId); // change to artiste profile

            if (confirmedRequest.getTipperEmail() != null && !confirmedRequest.getTipperEmail().isBlank()) // send receipt email if tipper email provided
                emailSvc.sendTemplateEmail(confirmedRequest, thankYouMessage);

            return ResponseEntity.ok().body(thankYouMessage);
        } else {
            throw new StripePaymentException("Payment failed. Please try again."); // only saving succesful payments
        }
    
    }

    // get tip by artiste id
    // returns tip object
    @GetMapping(path = "/get/{artisteId}")
    public ResponseEntity<List<Tip>> getTipsByArtisteId(
        @PathVariable String artisteId
    ) {
        
        logger.info(">>> Fetching tips for artiste with ID: %s".formatted(artisteId));
        List<Tip> tips = tipSvc.getTipsByArtisteId(artisteId);
        return ResponseEntity.ok().body(tips);

    }


}
