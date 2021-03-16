package in.springframework.learning.tutorial.exceptions;

import org.springframework.http.HttpStatus;

public class NoMatchingkeyCombinationsExist extends BaseException {
    public NoMatchingkeyCombinationsExist(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }

    public NoMatchingkeyCombinationsExist() {
    }
}
