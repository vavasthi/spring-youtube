package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.pojos.HelloWorldPojo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/hello")
public class HelloWorldEndpoint {

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<HelloWorldPojo> getHello() {
        HelloWorldPojo h = new HelloWorldPojo("John Doe");
        return Optional.of(h);
    }
}
