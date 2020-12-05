package in.springframework.learning.tutorial.security;

import java.security.Principal;
import java.util.Optional;

public class TokenPrincipal implements Principal {

    public TokenPrincipal(Optional<String> username, Optional<String> token) {
        this.username = username;
        this.token = token;
    }
    public TokenPrincipal(Optional<String> token) {
        this.username = token;
        this.token = token;
    }
    @Override
    public String getName() {
        return username.get();
    }

    public Optional<String> getUsername() {
        return username;
    }

    public void setUsername(Optional<String> username) {
        this.username = username;
    }

    public Optional<String> getToken() {
        return token;
    }

    public void setToken(Optional<String> token) {
        this.token = token;
    }

    private Optional<String> username;
    private Optional<String> token;
}
