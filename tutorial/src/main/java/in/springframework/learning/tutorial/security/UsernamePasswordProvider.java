package in.springframework.learning.tutorial.security;

import in.springframework.learning.tutorial.pojos.User;
import in.springframework.learning.tutorial.exceptions.UserAlreadyExists;
import in.springframework.learning.tutorial.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Optional;

public class UsernamePasswordProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordPrincipal principal
                = UsernamePasswordPrincipal.class.cast(authentication.getPrincipal());
        if (principal.isNewUser()) {

            String username = principal.getUsername().get();
            Optional<User> oue = userRepository.findUserByUsername(username);
            if (oue.isPresent()) {
                throw new UserAlreadyExists(username);
            }
            else {

                return new UsernamePasswordAuthenticationToken(principal,
                        null,
                        UserAuthority.getAuthoritiesFromRoles(Arrays.asList(Role.NEW_USER)));
            }
        }
        else if (principal.getUsername().isPresent() && principal.getPassword().isPresent()) {
            String username = principal.getUsername().get();
            String password = principal.getPassword().get();
            Optional<User> oue = userRepository.findUserByUsername(username);
            if (oue.isPresent()) {
                User ue = oue.get();
                if (passwordEncoder.matches(password, ue.getPassword())) {
                    return new UsernamePasswordAuthenticationToken(principal,
                            null,
                            UserAuthority.getAuthoritiesFromRoles(ue.getMask()));
                }
            }
            throw new BadCredentialsException(String.format("Username %s, authentication failure.", username));
        }
        throw new BadCredentialsException("Authenticaton request invalid, either username or password was not present");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}