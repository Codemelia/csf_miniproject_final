package csf.finalmp.app.server.repositories.unused;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import csf.finalmp.app.server.models.StripeTokens;

import static csf.finalmp.app.server.configs.RedisConfig.*;

import java.util.concurrent.TimeUnit;

@Repository
public class StripeRepository {

    @Autowired @Qualifier(REDIS_STRING)
    private RedisTemplate<String, String> template;

    private static final long STRIPE_ACCESS_EXPIRY = 3600; // 1 hour
    private static final long STRIPE_REFRESH_TOKEN_EXPIRY = 365 * 24 * 3600; // 1 year
    private static final String STRIPE_ACCESS_TOKEN_PREFIX = "stripe:access_token:";
    private static final String STRIPE_REFRESH_TOKEN_PREFIX = "stripe:refresh_token:";

    // STRIPE REDIS FUNCTIONS

    // save stripe access token under user id for 1 hour
    // SET stripe:access_token:<userId> <stripeAccessToken>>
    public void saveStripeAccessToken(StripeTokens tokens) {
        template.opsForValue().set(STRIPE_ACCESS_TOKEN_PREFIX + tokens.getArtisteId(), 
            tokens.getStripeAccessToken(), STRIPE_ACCESS_EXPIRY, TimeUnit.SECONDS);
    }

    // retrieve stripe access token under user id
    // GET stripe:access_token:<userId>
    public String getStripeAccessToken(String userId) {
        return template.opsForValue().get(STRIPE_ACCESS_TOKEN_PREFIX + userId);
    }

    // check if stripe access token exists in redis
    // EXISTS stripe:access_token:<userId>
    public boolean checkStripeAccessToken(String userId) {
        return template.hasKey(STRIPE_ACCESS_TOKEN_PREFIX + userId);
    }

    // save stripe refresh token under user id for 1 year
    // SET stripe:access_token:<userId> <stripeRefreshToken>
    public void saveStripeRefreshToken(StripeTokens tokens) {
        template.opsForValue().set(STRIPE_REFRESH_TOKEN_PREFIX + tokens.getArtisteId(),
            tokens.getStripeRefreshToken(), STRIPE_REFRESH_TOKEN_EXPIRY, TimeUnit.SECONDS);
    }

    // retrieve stripe refresh token under user id
    // GET stripe:refresh_token:<userId>
    public String getStripeRefreshToken(String userId) {
        return template.opsForValue().get(STRIPE_REFRESH_TOKEN_PREFIX + userId);
    }

    // check if stripe refresh token exists in redis
    // EXISTS stripe:refresh_token:<userId>
    public boolean checkStripeRefreshToken(String userId) {
        return template.hasKey(STRIPE_REFRESH_TOKEN_PREFIX + userId);
    }
    
    // UNUSED: remove both tokens from redis
    public void removesStripeTokens(String userId) {
        template.delete(STRIPE_ACCESS_TOKEN_PREFIX + userId);
        template.delete(STRIPE_REFRESH_TOKEN_PREFIX + userId);
    }
    
}
