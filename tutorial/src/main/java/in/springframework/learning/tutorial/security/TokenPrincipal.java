package in.springframework.learning.tutorial.security;

import java.security.Principal;
import java.util.Optional;

public class TokenPrincipal implements Principal {

    public TokenPrincipal(Optional<String> username, TYPE_OF_TOKEN typeOfToken, Optional<String> token) {
        this.username = username;
        this.typeOfToken = typeOfToken;
        this.token = token;
    }
    public TokenPrincipal(TYPE_OF_TOKEN typeOfToken, Optional<String> token) {
        this.username = token;
        this.typeOfToken = typeOfToken;
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

    public TYPE_OF_TOKEN getTypeOfToken() {
        return typeOfToken;
    }

    public void setTypeOfToken(TYPE_OF_TOKEN typeOfToken) {
        this.typeOfToken = typeOfToken;
    }

    private Optional<String> username;
    private Optional<String> token;
    private TYPE_OF_TOKEN typeOfToken;

    enum TYPE_OF_TOKEN {
        AUTH_TOKEN,
        REFRESH_TOKEN
    }
}
