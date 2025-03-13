package csf.finalmp.app.server.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.services.ArtisteService;
import csf.finalmp.app.server.services.StripeService;
import jakarta.servlet.http.HttpServletResponse;

@CrossOrigin(originPatterns = "http://localhost:4200", allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/stripe", produces = MediaType.APPLICATION_JSON_VALUE)
public class StripeController {

    private final ArtisteController artisteController;

    @Value("${frontend.app.url}")
    private String frontendBaseUrl; // for redirect after oauth callback

    @Autowired
    private StripeService stripeSvc;

    @Autowired
    private ArtisteService artisteSvc;

    private Logger logger = Logger.getLogger(StripeController.class.getName());

    StripeController(ArtisteController artisteController) {
        this.artisteController = artisteController;
    }

    @GetMapping(path = "/oauth-gen/{artisteId}")
    public ResponseEntity<String> genOAuthUrl(
        @PathVariable("artisteId") String artisteId) {
        
        logger.info(">>> Generating OAuth URL for artiste with ID: %s".formatted(artisteId));
        if (!artisteSvc.checkArtisteId(artisteId)) {
            throw new UserNotFoundException("Vibee could not be found.");
        }
        String oAuthUrl = stripeSvc.genOAuthUrl(artisteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(oAuthUrl);
    }

    // check if access token exists under artiste id
    @GetMapping(path = "/check-access/{artisteId}")
    public ResponseEntity<Boolean> getStripeAccountId(
        @PathVariable("artisteId") String artisteId) {

        logger.info(">>> Checking Stripe access token for artiste with ID: %s".formatted(artisteId));
        boolean stripeAccessValid = artisteSvc.checkArtisteStripeAccess(artisteId);
        return ResponseEntity.ok().body(stripeAccessValid);

    }

    // store oauth details and send a redirect back to dashboard on frontend app
    @GetMapping(path = "/oauth/callback")
    public void handleOAuthCallback(
        @RequestParam(value = "error", required = false) String error,
        @RequestParam(value = "code", required = false) String code,
        @RequestParam(value = "state", required = true) String state,
        HttpServletResponse response) throws Exception {

        if (error != null && error.equals("access_denied")) {
            response.sendRedirect("%s/dashboard".formatted(frontendBaseUrl));
        }

        logger.info(">>> Callback from Stripe OAuth received: %s | %s".formatted(code, state));
        stripeSvc.saveOAuthResponse(code, state, error); // exceptions will be handled by controller advice
        response.sendRedirect("%s/dashboard?%s".formatted(frontendBaseUrl, "stripeComplete=true"));

    }
    
}
