package in.springframework.learning.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExists extends BaseException {
    public UserAlreadyExists(String email, String username) {
        super(HttpStatus.CONFLICT.value(), String.format("Duplicate username %s or email %s", username, email));
        this.email = email;
        this.username = username;
    }
    public UserAlreadyExists(String username) {
        super(HttpStatus.CONFLICT.value(), String.format("Duplicate username ", username));
        this.username = username;
    }

    public UserAlreadyExists() {
    }
    private String email;
    private String username;
}
