package in.springframework.learning.tutorial.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.Id;
import java.util.List;

@Data
public class Specialization {
    @Id
    private String id;
    @Indexed
    private String code;
    private String name;
}
