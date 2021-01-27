package in.springframework.learning.tutorial.pojos;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.Id;
import java.util.List;

@Data
@Builder
public class Student {

    @Id
    private String id;
    @Indexed
    private String name;
    private String city;
}
