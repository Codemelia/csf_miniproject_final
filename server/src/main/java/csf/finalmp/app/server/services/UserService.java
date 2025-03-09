package csf.finalmp.app.server.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import csf.finalmp.app.server.exceptions.custom.InvalidCredentialsException;
import csf.finalmp.app.server.exceptions.custom.UserAlreadyExistsException;
import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.models.User;
import csf.finalmp.app.server.models.authentication.AuthRequest;
import csf.finalmp.app.server.repositories.UserRepository;
import csf.finalmp.app.server.utils.JwtUtil;

// FOR USER AUTH METHODS

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(UserService.class.getName());

    // check if table exists by checking count
    public boolean tableExists() {

        try {
            Integer count = userRepo.checkCount();
            logger.info(">>> MySQL: Users table SIZE: %d".formatted(count));
            return count != null && count >= 0;
        } catch (DataAccessException e) {
            logger.info(">>> MySQL: Users table does not exist");
            return false; // return false if data access exception occurs
        }

    }

    // create table
    public void createTable() {
        userRepo.createTable();
        logger.info(">>> MySQL: Users table created");
    }
    
    // insert user into table and retrieve id from db
    public Long registerUser(AuthRequest request) {

        // get auth request details
        String username = request.getUsername();
        String password = request.getPassword();
        String role = request.getRole();

        // check if username exists in db
        // returns true if > 0 rows found with username | false if none found
        boolean userExists = userRepo.userExists(request.getUsername()) > 0;

        // if user already exists in db, throw custom exception
        if(userExists) {
            throw new UserAlreadyExistsException("User already exists. Please use another username.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password)); // encode password before storage
        user.setRole(role);
        Long id = userRepo.saveUser(user);
        logger.info(
            ">>> AUTH: New user inserted with ID: %d".formatted(id));
        return id;

    }

    // login user and return generated token
    public String loginUser(AuthRequest request) {

        // get auth request details
        String username = request.getUsername();
        String password = request.getPassword();

        // check if username exists in db
        // returns true if > 0 rows found with username | false if none found
        boolean userExists = userRepo.userExists(username) > 0;

        // if user doesn't exist in db
        // throw custom user not found exception
        if (!userExists) {
            throw new UserNotFoundException("User not found. Please try again.");
        } 

        User user = userRepo.getUserByUsername(username); // get user from db
        System.out.println(user.toString());

        // if user not null and received password matches stored password
        if (user != null && encoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(username, user.getRole());
            logger.info(
                ">>> AUTH: User with ID %d login TOKEN: %s".formatted(user.getId(), token)
            );
            return token;
        } else {
            logger.info(
                ">>> AUTH: User with ID %d login failed".formatted(user.getId())
            );
            throw new InvalidCredentialsException(
                "Invalid credentials provided. Please try again.");
        }

    }

    // update user details
    public Long updateUser(AuthRequest request, Long userId) {

        // get auth request details
        String password = request.getPassword();
        String role = request.getRole();

        // check if username exists in db
        // returns true if > 0 rows found with username | false if none found
        boolean userExists = userRepo.userExists(request.getUsername()) > 0;

        // if user does not exist in db, throw custom exception
        if(!userExists) {
            throw new UserNotFoundException("User not found.");
        }

        User user = new User();
        user.setPassword(encoder.encode(password)); // encode password before storage
        user.setRole(role);
        user.setId(userId); // set id for validation before update
        userRepo.saveUser(user);
        logger.info(
            ">>> AUTH: Updated user with ID: %d".formatted(userId));
        return userId;

    }

}
