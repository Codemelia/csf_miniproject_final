package csf.finalmp.app.server.services;

import java.io.StringReader;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import csf.finalmp.app.server.exceptions.custom.StripePaymentException;
import csf.finalmp.app.server.models.ArtisteTransactionDetails;
import csf.finalmp.app.server.repositories.ArtisteTransactionRepository;
import jakarta.json.Json;
import jakarta.json.JsonObject;

// HANDLES STRIPE OAUTH

@Service
public class StripeService {

    @Value("${backend.app.url}")
    private String backendBaseUrl;

    @Value("${frontend.app.url}")
    private String frontendBaseUrl;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.client.id}")
    private String stripeClientId;

    @Autowired
    private ArtisteTransactionRepository artisteTransRepo;

    private final RestTemplate template = new RestTemplate();

    private static final String STRIPE_OAUTH_BASE_URL = "https://connect.stripe.com/oauth/authorize";
    private final String STRIPE_OAUTH_TOKEN_URL = "https://connect.stripe.com/oauth/token";

    private final Logger logger = Logger.getLogger(StripeService.class.getName());

    // generate url for oauth
    // https://connect.stripe.com/oauth/authorize?response_type=code&client_id=stripe_client_id&scope=read_write&redirect_uri=http://backend-url/oauth/callback&state=123
    public String genOAuthUrl(String artisteId) {

        return UriComponentsBuilder.fromUriString(STRIPE_OAUTH_BASE_URL)
            .queryParam("response_type", "code")
            .queryParam("client_id", stripeClientId)
            .queryParam("scope", "read_write")
            .queryParam("refresh_uri", String.format("%s/dashboard/overview",
                frontendBaseUrl))
            .queryParam("redirect_uri", String.format("%s/api/stripe/oauth/callback",
                backendBaseUrl)) // server endpoint for callback
            .queryParam("state", artisteId) // send id as state for later verification
            .toUriString();

    }

    // handle oauth callback
    public void saveOAuthResponse(String code, String state) throws Exception {

        // ensure type safety
        String artisteId = String.valueOf(state);

        // exchange code for access token and accountId
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("client_secret", stripeSecretKey);

        // create http headers 
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // create http entity
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        // make api call
        ResponseEntity<String> response = template.exchange(
            STRIPE_OAUTH_TOKEN_URL,
            HttpMethod.POST,
            entity,
            String.class
        );

        logger.info(">>> Received Stripe OAuth Response: %s".formatted(response.getBody()));

        // check response and return if avail
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            logger.severe(">>> Stripe OAuth Error: %s".formatted(response.getStatusCode().toString()));
            throw new StripePaymentException("An unexpected error occurred. Please try again.");
        }

        String responseBody = response.getBody();

        // parse json response
        JsonObject responseJson = Json.createReader(new StringReader(responseBody))
            .readObject();
        String stripeAccessToken = responseJson.getString("access_token");
        String stripeAccountId = responseJson.getString("stripe_user_id");
        String stripeRefreshToken = responseJson.getString("refresh_token");
        logger.info(">>> Parsed from Stripe OAuth Response: Access Token: %s | Refresh Token: %s | Account ID: %s"
            .formatted(stripeAccessToken, stripeRefreshToken, stripeAccountId));

        // save stripe account id
        ArtisteTransactionDetails artiste = new ArtisteTransactionDetails();
        artiste.setArtisteId(artisteId);
        artiste.setStripeAccountId(stripeAccountId);

        // save stripe access token and refresh token
        artiste.setStripeAccessToken(stripeAccessToken);
        artiste.setStripeRefreshToken(stripeRefreshToken);
        artisteTransRepo.saveArtisteStripe(artiste);

    }
    
}
