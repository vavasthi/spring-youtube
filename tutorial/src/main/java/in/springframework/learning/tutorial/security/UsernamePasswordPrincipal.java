package in.springframework.learning.tutorial.security;

import java.security.Principal;
import java.util.Optional;

public class UsernamePasswordPrincipal implements Principal {

    public UsernamePasswordPrincipal(Optional<String> username,
                                     Optional<String> password) {
        this.username = username;
        this.password = password;
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getPassword() {
        return password;
    }

    @Override
    public String getName() {
        return username.get();
    }
    private final Optional<String> username;
    private final Optional<String> password;
}
