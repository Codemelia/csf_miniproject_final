package csf.finalmp.app.server.configs;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.client.id}")
    private String stripeClientId;

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = stripeSecretKey;
        Stripe.clientId = stripeClientId;
    }

}
