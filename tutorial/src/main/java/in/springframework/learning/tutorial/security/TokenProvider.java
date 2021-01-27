package in.springframework.learning.tutorial.security;

import in.springframework.learning.tutorial.pojos.User;
import in.springframework.learning.tutorial.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.transaction.Transactional;
import java.util.*;

public class TokenProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TokenPrincipal principal = TokenPrincipal.class.cast(authentication.getPrincipal());
        if (principal.getUsername().isPresent() && principal.getToken().isPresent()) {
            String username = principal.getUsername().get();
            String token = principal.getToken().get();
            Optional<User> oue = Optional.empty();
            if (principal.getTypeOfToken().equals(TokenPrincipal.TYPE_OF_TOKEN.REFRESH_TOKEN)) {

                oue = userRepository.findUserByRefreshToken(token);
            }
            else {

                oue = userRepository.findUserByAuthToken(token);
            }
            if (oue.isPresent()) {
                User ue = oue.get();
                username = ue.getUsername();
                if (principal.getTypeOfToken().equals(TokenPrincipal.TYPE_OF_TOKEN.REFRESH_TOKEN)) {
                    if (ue.getRefreshExpiry().after(new Date())) {

                        return new PreAuthenticatedAuthenticationToken(new TokenPrincipal(Optional.of(ue.getUsername()),
                                TokenPrincipal.TYPE_OF_TOKEN.AUTH_TOKEN,
                                principal.getToken()),
                                null,
                                UserAuthority.getAuthoritiesFromRoles(Role.REFRESH.getMask()));
                    }
                }
                else {
                    if (ue.getExpiry().after(new Date())) {

                        return new PreAuthenticatedAuthenticationToken(new TokenPrincipal(Optional.of(ue.getUsername()),
                                TokenPrincipal.TYPE_OF_TOKEN.AUTH_TOKEN,
                                principal.getToken()),
                                null,
                                UserAuthority.getAuthoritiesFromRoles(ue.getMask()));
                    }
                }

            }
            throw new BadCredentialsException(String.format("Username %s, authentication failure.", username));
        }
        throw new BadCredentialsException("Authenticaton request invalid, either token was not present");
    }

    private String generateToken(String username) {

        return Base64.getEncoder().encodeToString(username.getBytes()) + "-" + UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
