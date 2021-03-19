package in.springframework.learning.tutorial.pojos;

import in.springframework.learning.tutorial.annotations.CacheCascade;
import in.springframework.learning.tutorial.annotations.CachesCascade;
import in.springframework.learning.tutorial.annotations.RelatedCaches;
import in.springframework.learning.tutorial.caching.Contained1CacheService;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class ContainerEntity extends CachedEntity<String> implements Serializable {

    @Id
    private String id;
    private String name;
    private String description;
    private Set<String> contained1;
    private Set<String> contained2;
}
