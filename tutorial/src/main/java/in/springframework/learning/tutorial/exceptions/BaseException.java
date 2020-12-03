package in.springframework.learning.tutorial.exceptions;

public class BaseException extends RuntimeException {

    private int errorCode;

    public BaseException() {
        super("");
    }
    public BaseException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public BaseException(Throwable cause) {
        super(cause);
    }
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
