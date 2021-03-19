package in.springframework.learning.tutorial.pojos;

import in.springframework.learning.tutorial.caching.AbstractCacheService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassAttributePair implements Serializable {
    private String attribute;
    private Class<? extends AbstractCacheService> clazz;
}
