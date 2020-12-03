package in.springframework.learning.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserDoesnotExist extends BaseException {
    public UserDoesnotExist(Long id) {
        super(HttpStatus.NOT_FOUND.value(), String.format("User id %d does not exist.", id));
        this.id = id;
    }

    public UserDoesnotExist() {
    }
    private Long id;
}
