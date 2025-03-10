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
    public Long registerUser(User user) {

        // check if username exists in db
        // returns true if > 0 rows found with email | false if none found
        boolean emailExists = userRepo.emailExists(user.getEmail()) > 0;

        // if user already exists in db, throw custom exception
        if(emailExists) {
            throw new UserAlreadyExistsException("User already exists. Please use another email.");
        }

        user.setPassword(encoder.encode(user.getPassword())); // encode password before storage
        user.setId(null); // set id to null to ensure it is treated asa a registration
        Long id = userRepo.saveUser(user);
        logger.info(
            ">>> AUTH: New user inserted with ID: %d".formatted(id));
        return id;

    }

    // login user and return generated token and id
    public String loginUser(User user) {

        // get auth request details
        String email = user.getEmail();
        String password = user.getPassword();

        // check if username exists in db
        // returns true if > 0 rows found with email | false if none found
        boolean emailExists = userRepo.emailExists(email) > 0;

        // if user doesn't exist in db
        // throw custom user not found exception
        if (!emailExists) {
            throw new UserNotFoundException("User not found. Please try again.");
        } 

        User fullUser = userRepo.getUserByEmail(email); // get user from db
        System.out.println(user.toString());

        // if user not null and received password matches stored password
        if (fullUser != null && encoder.matches(password, fullUser.getPassword())) {
            String token = jwtUtil.generateToken(fullUser.getId(), fullUser.getRole()); // gen jwt token every time user logs in
            logger.info(
                ">>> AUTH: User logged in with TOKEN: %s".formatted(token));
            return token;
        } else {
            logger.info(
                ">>> AUTH: User with ID %d login failed".formatted(fullUser.getId())
            );
            throw new InvalidCredentialsException(
                "Invalid credentials provided. Please try again.");
        }

    }

    // update user details
    public Long updateUser(User user, Long userId) {

        // check if username exists in db
        // returns true if > 0 rows found with username | false if none found
        boolean userExists = userRepo.userExists(user.getUsername()) > 0;

        // if user does not exist in db, throw custom exception
        if(!userExists) {
            throw new UserNotFoundException("User not found.");
        }

        user.setPassword(encoder.encode(user.getPassword())); // encode password before storage
        user.setId(userId); // set id for validation before update
        userRepo.saveUser(user);
        logger.info(
            ">>> AUTH: Updated user with ID: %d".formatted(userId));
        return userId;

    }

}
