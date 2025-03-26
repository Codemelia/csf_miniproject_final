package csf.finalmp.app.server.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.services.ArtisteProfileService;
import csf.finalmp.app.server.services.ArtisteTransactionService;
import csf.finalmp.app.server.services.StripeService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "/api/stripe", produces = MediaType.APPLICATION_JSON_VALUE)
public class StripeController {

    @Value("${frontend.app.url}")
    private String frontendBaseUrl; // for redirect after oauth callback

    @Autowired
    private StripeService stripeSvc;

    @Autowired
    private ArtisteProfileService artisteProfSvc;

    @Autowired
    private ArtisteTransactionService artisteTransSvc;

    // @Value("${stripe.webhook.secret}")
    // private String webhookSecret;

    private Logger logger = Logger.getLogger(StripeController.class.getName());

    @GetMapping(path = "/gen-oauth/{artisteId}")
    public ResponseEntity<String> genOAuthUrl(
        @PathVariable("artisteId") String artisteId) {
        
        logger.info(">>> Generating Stripe OAuth URL for artiste ID: %s".formatted(artisteId));
        if (!artisteProfSvc.checkArtisteId(artisteId)) {
            throw new UserNotFoundException("Vibee could not be found.");
        }
        String oAuthUrl = stripeSvc.genOAuthUrl(artisteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(oAuthUrl);
    }

    // store oauth details and send a redirect back to dashboard on frontend app
    @GetMapping(path = "/oauth/callback")
    public void handleOAuthCallback(
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "code", required = false) String code,
        @RequestParam(value = "state", required = true) String state,
        HttpServletResponse response) throws Exception {

        if (error != null || code == null || state == null) {
            logger.warning(">>> Stripe error occurred");
            response.sendRedirect("%s/dashboard/overview".formatted(frontendBaseUrl));
            return;
        }

        logger.info(">>> Callback from Stripe OAuth received: %s | %s".formatted(code, state));
        stripeSvc.saveOAuthResponse(code, state);
        response.sendRedirect("%s/dashboard/overview".formatted(frontendBaseUrl));

    }

    // check if access token exists under artiste id
    @GetMapping(path = "/check-access/{artisteId}")
    public ResponseEntity<Boolean> getStripeAccountId(
        @PathVariable("artisteId") String artisteId) {

        logger.info(">>> Checking Stripe access token for artiste with ID: %s".formatted(artisteId));
        boolean stripeAccessValid = artisteTransSvc.checkArtisteStripeAccess(artisteId);
        return ResponseEntity.ok().body(stripeAccessValid);

    }

}
