package csf.finalmp.app.server.exceptions;

// PURPOSE OF THIS EXCEPTION CLASS
// CUSTOM INCORRECT STRIPE PARAMS EXCEPTION

public class StripeParamException extends RuntimeException {
    
    public StripeParamException(String message) {
        super(message);
    }

}
