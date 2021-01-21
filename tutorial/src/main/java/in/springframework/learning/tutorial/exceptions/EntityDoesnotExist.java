package in.springframework.learning.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityDoesnotExist extends BaseException {
    public EntityDoesnotExist(String message) {
        super(HttpStatus.NOT_FOUND.value(), message);
        this.id = id;
    }

    public EntityDoesnotExist() {
    }
    private Long id;
}
