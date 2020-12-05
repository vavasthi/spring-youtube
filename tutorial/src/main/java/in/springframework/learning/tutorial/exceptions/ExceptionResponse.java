package in.springframework.learning.tutorial.exceptions;

public class ExceptionResponse {
    public ExceptionResponse(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ExceptionResponse() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private int errorCode;
    private String message;
}
