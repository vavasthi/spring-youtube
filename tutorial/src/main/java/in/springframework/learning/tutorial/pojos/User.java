package in.springframework.learning.tutorial.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.*;
import java.util.Date;

@Data
public class User {
    @Id
    private String id;
    private String fullname;
    @Column(length = 64)
    @Indexed(unique = true)
    private String username;
    private String password;
    @Column(length = 128)
    @Indexed(unique = true)
    private String email;
    private long mask;
    @Column(name = "auth_token")
    @Indexed(unique = true)
    private String authToken;
    private Date expiry;
    @Column(name = "refresh_token")
    @Indexed(unique = true)
    private String refreshToken;
    @Column(name = "refresh_expiry")
    private Date refreshExpiry;
}
