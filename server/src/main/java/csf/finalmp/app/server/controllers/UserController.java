package csf.finalmp.app.server.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import csf.finalmp.app.server.models.User;
import csf.finalmp.app.server.models.authentication.AuthResponse;
import csf.finalmp.app.server.services.UserService;

import csf.finalmp.app.server.exceptions.custom.UserAuthenticationException;

@CrossOrigin(originPatterns = "http://localhost:4200", allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserService userSvc;

    // to ensure proper tracking
    private Logger logger = Logger.getLogger(UserController.class.getName());

    // register user and return feedback by validating returned id
    // sql exceptions will be caught by global exception handler
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        
        logger.info(">>> Registering USER: %s".formatted(user.getEmail()));
        Long id = userSvc.registerUser(user); // convert auth request to user and save to db
        if (id != null && id > 0) { // id starts from 1
            return ResponseEntity.status(HttpStatus.CREATED).body(
                AuthResponse.forRegistration(
                    id, "User registration was successful"));
        } else {
            throw new UserAuthenticationException("User registration failed");
        }

    }

    // login user and return token string
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(@RequestBody User user) {
        logger.info(">>> Logging in USER: %s".formatted(user.getUsername()));
        String token = userSvc.loginUser(user); // converts request to user and validates
        if (token != null && !token.isBlank()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                AuthResponse.forLogin(token, "User login was successful"));
        } else {
            throw new UserAuthenticationException("User login failed");
        }
    }

    @PostMapping(path = "/update/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> register(
        @RequestBody User user,
        @PathVariable Long userId) {
        
        logger.info(">>> Registering USER: %s".formatted(user.getUsername()));
        try { 
            userSvc.updateUser(user, userId); // convert auth request to user and save to db
            return ResponseEntity.status(HttpStatus.CREATED).body(
                AuthResponse.forRegistration(
                    userId, "User update was successful"));
        } catch (Exception e) {
            throw e; // let controller advice handle
        }

    }
    
}
