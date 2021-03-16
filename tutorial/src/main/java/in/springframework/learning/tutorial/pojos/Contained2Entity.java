package in.springframework.learning.tutorial.pojos;

import in.springframework.learning.tutorial.annotations.RelatedCache;
import in.springframework.learning.tutorial.annotations.RelatedCaches;
import in.springframework.learning.tutorial.caching.Contained1CacheService;
import lombok.Data;

import javax.persistence.Id;

@Data
@RelatedCaches(name = "contained2", caches = {@RelatedCache(primaryKeyField = "contained1", clazz = Contained1CacheService.class)})
public class Contained2Entity {

    @Id
    private String id;
    private String name;
    private String description;
    private String containerId;
}
