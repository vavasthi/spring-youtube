package in.springframework.learning.tutorial.pojos;

import lombok.Data;

import javax.persistence.Id;
import java.util.List;

@Data
public class Department {
    @Id
    private String id;
    private String name;
}
