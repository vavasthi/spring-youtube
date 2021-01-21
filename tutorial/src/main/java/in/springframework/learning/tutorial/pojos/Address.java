package in.springframework.learning.tutorial.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.Id;

@Data
public class Address {

    @Id
    private String id;
    @Indexed
    private Long userId;
    private String address1;
    private String address2;
    private PinCodeAndCity pinCodeAndCity;
}
