package csf.finalmp.app.server.exceptions.custom;

// PURPOSE OF THIS EXCEPTION CLASS
// CUSTOM INCORRECT STRIPE PARAMS EXCEPTION

public class StripePaymentException extends RuntimeException {
    
    public StripePaymentException(String message) {
        super(message);
    }

}
