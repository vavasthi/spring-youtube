package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.pojos.User;
import in.springframework.learning.tutorial.pojos.LoginRequest;
import in.springframework.learning.tutorial.pojos.LoginResponse;
import in.springframework.learning.tutorial.repositories.UserRepository;
import in.springframework.learning.tutorial.security.TokenPrincipal;
import in.springframework.learning.tutorial.security.UsernamePasswordPrincipal;
import in.springframework.learning.tutorial.utils.ExpiryValues;
import in.springframework.learning.tutorial.utils.TokenUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

            Optional<User> oue = userRepository.findUserByUsername(principal.getName());
            if (oue.isPresent()) {
                User ue = oue.get();
                refreshTokens(ue);
                return new LoginResponse(loginRequest.getUsername(),
                        ue.getAuthToken(),
                        ue.getExpiry(),
                        ue.getRefreshToken(),
                        ue.getRefreshExpiry());
            }
        }
        throw new BadCredentialsException(String.format("Authentication endpoint FATAL error for user %s", principal.getName()));
    }

    @RequestMapping(value = "/validate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse validated(Authentication authentication) {

        TokenPrincipal principal
                = TokenPrincipal.class.cast(authentication.getPrincipal());
        Optional<User> oue = userRepository.findUserByUsername(principal.getName());
        if (oue.isPresent()) {
            User ue = oue.get();
            return new LoginResponse(principal.getUsername().get(),
                    ue.getAuthToken(),
                    ue.getExpiry(),
                    ue.getRefreshToken(),
                    ue.getRefreshExpiry());
        }
        throw new BadCredentialsException(String.format("Authentication endpoint FATAL error for user %s", principal.getName()));
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse refresh(Authentication authentication) {

        TokenPrincipal principal
                = TokenPrincipal.class.cast(authentication.getPrincipal());
        Optional<User> oue = userRepository.findUserByUsername(principal.getName());
        if (oue.isPresent()) {
            User ue = oue.get();
            refreshTokens(ue);
            return new LoginResponse(principal.getUsername().get(),
                    ue.getAuthToken(),
                    ue.getExpiry(),
                    ue.getRefreshToken(),
                    ue.getRefreshExpiry());
        }
        throw new BadCredentialsException(String.format("Authentication endpoint FATAL error for user %s", principal.getName()));
    }

    private void refreshTokens(User ue) {

        String authToken = TokenUtilities.generateToken();
        String refreshToken = TokenUtilities.generateToken();
        ExpiryValues ev = TokenUtilities.generateTokenExiry();
        ue.setAuthToken(authToken);
        ue.setRefreshToken(refreshToken);
        ue.setExpiry(ev.getExpiry());
        ue.setRefreshExpiry(ev.getRefreshExpiry());
        userRepository.save(ue);
    }
}
