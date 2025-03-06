package csf.finalmp.app.server.exceptions;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import redis.clients.jedis.exceptions.JedisConnectionException;

// PURPOSE OF THIS HANDLER
// HANDLE REST ENDPOINT EXCEPTIONS

@RestControllerAdvice
public class GlobalExceptionHandler {

    // handle custom exception
    @ExceptionHandler(MusicianNotFoundException.class)
    public ResponseEntity<ApiError> handleMusicianNotFound(MusicianNotFoundException e) {
        ApiError error = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
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
