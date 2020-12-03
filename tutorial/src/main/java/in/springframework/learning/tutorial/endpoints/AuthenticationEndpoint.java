package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.entities.UserEntity;
import in.springframework.learning.tutorial.pojos.LoginRequest;
import in.springframework.learning.tutorial.pojos.LoginResponse;
import in.springframework.learning.tutorial.repositories.UserRepository;
import in.springframework.learning.tutorial.security.UsernamePasswordPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.Optional;


@RestController
@RequestMapping("/authenticate")
public class AuthenticationEndpoint {

    @Autowired
    private UserRepository userRepository;
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse login(@RequestBody LoginRequest loginRequest,
                               Authentication authentication) {

        UsernamePasswordPrincipal principal
                = UsernamePasswordPrincipal.class.cast(authentication.getPrincipal());
        if(loginRequest.getUsername().equals(principal.getName())) {

            Optional<UserEntity> oue = userRepository.findUserByUsername(principal.getName());
            if (oue.isPresent()) {
                UserEntity ue = oue.get();
                return new LoginResponse(loginRequest.getUsername(), ue.getAuthToken(), ue.getExpiry());
            }
        }
        throw new BadCredentialsException(String.format("Authentication endpoint FATAL error for user %s", principal.getName()));
    }
}
