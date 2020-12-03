package in.springframework.learning.tutorial.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user", uniqueConstraints =
        {
                @UniqueConstraint(name = "uq_email", columnNames = {"email"}),
                @UniqueConstraint(name = "uq_username", columnNames = {"username"})
        })
public class UserEntity {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getMask() {
        return mask;
    }

    public void setMask(long mask) {
        this.mask = mask;
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

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String fullname;
    @Column(length = 64)
    private String username;
    private String password;
    @Column(length = 128)
    private String email;
    private long mask;
    private String authToken;
    private Date expiry;
}
