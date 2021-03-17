package in.springframework.learning.tutorial.pojos;

import in.springframework.learning.tutorial.annotations.RelatedCaches;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

@Data
public class ContainerEntity implements Serializable {

    @Id
    private String id;
    private String name;
    private String description;
    private List<String> contained1;
    private List<String> contained2;
}
