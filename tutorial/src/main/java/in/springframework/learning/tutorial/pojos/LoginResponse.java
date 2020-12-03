package in.springframework.learning.tutorial.pojos;

import java.util.Date;

public class LoginResponse {

    public LoginResponse() {
    }

    public LoginResponse(String username, String authToken, Date expiry) {
        this.username = username;
        this.authToken = authToken;
        this.expiry = expiry;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    private String username;
    private String authToken;
    private Date expiry;
}
