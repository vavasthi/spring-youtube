package in.springframework.learning.tutorial.pojos;

import lombok.Data;

import javax.persistence.Id;
import java.util.List;

@Data
public class Faculty {
    @Id
    private String id;
    private String name;
    private String title;
    private String departmentId;
    private List<String> specializations;
}
