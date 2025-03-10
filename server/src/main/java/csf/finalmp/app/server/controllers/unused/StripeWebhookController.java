package csf.finalmp.app.server.controllers.unused;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import csf.finalmp.app.server.services.TipService;
/*
@RestController
@RequestMapping("/api/webhooks/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Autowired
    private TipService tipSvc;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(StripeWebhookController.class.getName());

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws SignatureVerificationException {

        // verify the webhook signature
        Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

        // handle the event
        PaymentIntent paymentIntent;
        switch (event.getType()) {

            case "payment_intent.canceled":
                paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject();
                handlePaymentIntentSucceeded(paymentIntent);
                break;

            case "payment_intent.created":
                paymentIntent = (PaymentIntent) event.getData().getObject();
                handlePaymentIntentSucceeded(paymentIntent);
                break;

            case "payment_intent.partially_funded":
                paymentIntent = (PaymentIntent) event.getData().getObject();
                handlePaymentIntentSucceeded(paymentIntent);
                break;

            case "payment_intent.payment_failed":
                paymentIntent = (PaymentIntent) event.getData().getObject();
                handlePaymentIntentFailed(paymentIntent);
                break;

            case "payment_intent.processing":
                paymentIntent = (PaymentIntent) event.getData().getObject();
                handlePaymentIntentFailed(paymentIntent);
                break;

            case "payment_intent.requires_action":
                paymentIntent = (PaymentIntent) event.getData().getObject();
                handlePaymentIntentFailed(paymentIntent);
                break;

            case "payment_intent.succeeded":
                paymentIntent = (PaymentIntent) event.getData().getObject();
                handlePaymentIntentSucceeded(paymentIntent);
                break;

            default:
                // Log or handle unexpected events
                logger.info("Unhandled event type: " + event.getType());
        }

        // Return a 200 response to acknowledge receipt of the event
        return ResponseEntity.ok("Event received");

    }

    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        logger.info(">>> Payment Intent succeeded: " + paymentIntent.getId());
        tipSvc.updateTip(paymentIntent.getId(), "succeeded");
    }

    private void handlePaymentIntentFailed(PaymentIntent paymentIntent) {
        logger.info(">>> Payment Intent failed: " + paymentIntent.getId());
        tipSvc.updateTip(paymentIntent.getId(), "failed");
    }
}
*/