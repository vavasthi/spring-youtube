package in.springframework.learning.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserCreationUsernameMismatchException extends BaseException {
    public UserCreationUsernameMismatchException(String usernameInHeader, String usernameInBody) {
        super(HttpStatus.CONFLICT.value(),
                String.format("Username mismatch in header (%s) and body (%s).",
                        usernameInHeader,
                        usernameInBody));
        this.usernameInHeader = usernameInHeader;
        this.usernameInBody = usernameInBody;
    }

    public String getUsernameInHeader() {
        return usernameInHeader;
    }

    public void setUsernameInHeader(String usernameInHeader) {
        this.usernameInHeader = usernameInHeader;
    }

    public String getUsernameInBody() {
        return usernameInBody;
    }

    public void setUsernameInBody(String usernameInBody) {
        this.usernameInBody = usernameInBody;
    }

    public UserCreationUsernameMismatchException() {
    }
    private String usernameInHeader;
    private String usernameInBody;
}
