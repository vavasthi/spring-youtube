package in.springframework.learning.tutorial.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class CacheKeyAnnotationAbsent extends BaseException {
    public CacheKeyAnnotationAbsent(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }

    public CacheKeyAnnotationAbsent() {
    }
}
