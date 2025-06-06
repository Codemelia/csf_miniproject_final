package csf.finalmp.app.server.configs;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

// PURPOSE OF THIS COMPONENT
// HELPER METHODS FOR AUTH SERVICE

@Component
public class JwtUtil {

    // secret key
    @Value("${jwt.secret.key}")
    private String jwtKey;
    
    // conv jwt key to secret key
    private SecretKey secretKey;
    private JwtParser jwtParser;

    // expiration time for token generation
    private static final int EXPIRATION_TIME = 86400000; // 24 hours

    @PostConstruct
    public void init() {
        System.out.println("JWT Secret Key: " + jwtKey);
        if (jwtKey == null || jwtKey.length() < 64) {
            throw new IllegalArgumentException("JWT secret key is too short. Use at least 64 characters.");
        }
        // secret key and parser
        secretKey = Keys.hmacShaKeyFor(jwtKey.getBytes());
        jwtParser = Jwts.parser().verifyWith(secretKey).build();
    }

    // generate token
    public String generateToken(String userId, String role) {

        // set username and role with curr date as issue date
        // set expiration 24 hours from curr date
        // sign with jwt key
        return Jwts.builder()
            .subject(userId)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(secretKey, Jwts.SIG.HS512)
            .compact();

    }

    // get user ID by token
    public String getUserId(String token) {
        return jwtParser.parseSignedClaims(token).getPayload().getSubject();
    }

    // get role by token
    public String getRoleByToken(String token) {
        return jwtParser.parseSignedClaims(token).getPayload()
            .get("role", String.class);
    }

    // validate token
    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            return true; // returns true if token exists under jwtKey
        } catch (Exception e) {
            return false; // returns false if exception occurs (token doesn't exist)
        }
    }
    
}
