package in.springframework.learning.tutorial.security;

import in.springframework.learning.tutorial.entities.UserEntity;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class UsernamePasswordProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordPrincipal principal = UsernamePasswordPrincipal.class.cast(authentication.getPrincipal());
        if (principal.getUsername().isPresent() && principal.getPassword().isPresent()) {
            String username = principal.getUsername().get();
            String password = principal.getPassword().get();
            Optional<UserEntity> oue = userRepository.findUserByUsername(username);
            if (oue.isPresent()) {
                UserEntity ue = oue.get();
                if (passwordEncoder.matches(password, ue.getPassword())) {

                    ue.setAuthToken(generateToken());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.HOUR, 24);
                    ue.setExpiry(calendar.getTime());
                    return new UsernamePasswordAuthenticationToken(principal,
                            null,
                            UserAuthority.getAuthoritiesFromRoles(ue.getMask()));
                }
            }
            throw new BadCredentialsException(String.format("Username %s, authentication failure.", username));
        }
        throw new BadCredentialsException("Authenticaton request invalid, either username or password was not present");
    }

    private String generateToken() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
