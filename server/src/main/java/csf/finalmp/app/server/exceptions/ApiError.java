package csf.finalmp.app.server.exceptions;

import java.time.LocalDateTime;

// PURPOSE OF THIS MODEL
// MAP ERROR DETAILS TO OBJECT FOR CLIENT DISPLAY

public class ApiError {

    // variables
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    // constructors
    public ApiError() {}
    public ApiError(int status, String error, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    // getters and setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    // to string
    @Override
    public String toString() {
        return "ApiError [timestamp=" + timestamp + ", status=" + status + ", error=" + error + ", message=" + message
                + "]";
    }

}
