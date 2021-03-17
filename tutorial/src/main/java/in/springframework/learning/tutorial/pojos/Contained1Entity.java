package in.springframework.learning.tutorial.pojos;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

@Data
public class Contained1Entity extends CachedEntity<String> implements Serializable  {

    @Id
    private String id;
    private String name;
    private String description;
    private String containerId;
}
