package in.springframework.learning.tutorial.pojos;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
public class Contained2Entity extends CachedEntity<String> implements Serializable {

    @Id
    private String id;
    private String name;
    private String description;
    private String containerId;
}
