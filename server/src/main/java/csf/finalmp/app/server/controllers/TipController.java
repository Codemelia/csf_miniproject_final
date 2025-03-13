package csf.finalmp.app.server.controllers;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import csf.finalmp.app.server.models.Tip;
import csf.finalmp.app.server.models.TipRequest;
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

    // insert tip from client
    // returns client secret
    @PostMapping(path="/process", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> processTip(
        @RequestBody TipRequest request) throws StripeException { // throw exception for global handler

        logger.info(">>> Processing tip request: %s".formatted(request.toString()));
        Map<String, Object> response = tipSvc.processTip(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    // update payment status
    // if payment status successful, add tip to wallet
    @PutMapping("/confirm/{paymentIntentId}")
    public ResponseEntity<String> updateTip(
        @PathVariable String paymentIntentId,
        @RequestBody PaymentIntent paymentIntent) {

        logger.info(">>> Processing tip confirmation for Payment Intent: %s".formatted(paymentIntentId));
        String paymentStatus = paymentIntent.getStatus();

        Tip tip = tipSvc.updateTip(paymentIntentId, paymentStatus);
        if (paymentStatus.contains("succeeded")) {
            tipSvc.addTipToWallet(tip.getArtisteId(), null);
        }
        return ResponseEntity.ok("Payment confirmed successfully");
    
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
