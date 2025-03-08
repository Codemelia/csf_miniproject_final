package csf.finalmp.app.server.exceptions;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.stripe.exception.StripeException;

import csf.finalmp.app.server.exceptions.custom.InvalidCredentialsException;
import csf.finalmp.app.server.exceptions.custom.MusicianNotFoundException;
import csf.finalmp.app.server.exceptions.custom.StripeParamException;
import csf.finalmp.app.server.exceptions.custom.UserAlreadyExistsException;
import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.exceptions.custom.UserAuthenticationException;

import redis.clients.jedis.exceptions.JedisConnectionException;

// PURPOSE OF THIS HANDLER
// HANDLE REST ENDPOINT EXCEPTIONS

@RestControllerAdvice
public class GlobalExceptionHandler {

    // handle custom auth exception
    @ExceptionHandler(UserAuthenticationException.class)
    public ResponseEntity<ApiError> handleUserAuthenticationException(UserAuthenticationException e) {

        String message = e.getMessage();
        if (message.contains("registration")) {
            ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(), 
                "User Registration Error", 
                message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
        } else {
            ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED.value(), 
                "User Login Error", 
                message);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(error);
        }
        
    }

    // handle custom username already exists exception
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(), 
            "User Already Exists Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(error);
    }
    
    // handle custom username not found exception
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException e) {
        ApiError error = new ApiError(
            HttpStatus.NOT_FOUND.value(), 
            "User Not Found Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(error);
    }

    // handle custom invalid login credentials exception
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentialsException(InvalidCredentialsException e) {
        ApiError error = new ApiError(
            HttpStatus.UNAUTHORIZED.value(), 
            "Invalid Login Credentials Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(error);
    }

    // handle custom stripe exception
    @ExceptionHandler(StripeParamException.class)
    public ResponseEntity<ApiError> handleStripeParamException(StripeParamException e) {
        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(), 
            "Stripe Parameter Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    // handle stripe exception
    // handle all unexpected errors
    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ApiError> handleStripeException(StripeException e) {
        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(), 
            "Stripe Payment Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(error);
    }

    // handle custom exception
    @ExceptionHandler(MusicianNotFoundException.class)
    public ResponseEntity<ApiError> handleMusicianNotFound(MusicianNotFoundException e) {
        ApiError error = new ApiError(
            HttpStatus.NOT_FOUND.value(), 
            "Musician Not Found Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(error);
    }

    // handle any data access exception
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDataAccessException(DataAccessException e) {
        ApiError error = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "Database Access Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }

    // handle any SQL exception that is not wrapped in data access exception
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiError> handleSQLException(SQLException e) {
        ApiError error = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "SQL Database Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }

    // handle redis connection errors
    @ExceptionHandler(JedisConnectionException.class)
    public ResponseEntity<ApiError> handleJedisConnectionException(JedisConnectionException e) {
        ApiError error = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "Redis Connection Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }

    // handle general errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        ApiError error = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "Internal Server Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }

    // handle all unexpected errors
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleThrowable(Throwable e) {
        ApiError error = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "Unexpected Error", 
            e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }
    
}
