package in.springframework.learning.tutorial.caching;

import in.springframework.learning.tutorial.annotations.DefineCache;
import in.springframework.learning.tutorial.configurations.FirstRedisConfiguration;
import in.springframework.learning.tutorial.pojos.Contained1Entity;
import in.springframework.learning.tutorial.repositories.Contained1EntityRepository;
import in.springframework.learning.tutorial.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@DefineCache(name = Constants.CONTAINED_1_CACHE_NAME,
        prefix = Constants.CONTAINED_1_CACHE_PREFIX,
        expiry = Constants.CONTAINED_1_CACHE_EXPIRY)
@Service
public class Contained1CacheService extends Physical1CacheService<String, Contained1Entity> {
    @Autowired
    private Contained1EntityRepository repository;
    @Override
    protected Contained1Entity findById(String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try {
            return getObject(id, Contained1Entity.class);
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        Optional<Contained1Entity> optionalEntity = repository.findById(id);
        if (optionalEntity.isPresent()) {
            Contained1Entity entity = optionalEntity.get();
            storeObject(entity.getId(), entity);
            return entity;
        }
        return null;
    }

    @Override
    protected Contained1Entity evict(String id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Contained1Entity entity = findById(id);
        if (entity != null) {

            evictObject(id, entity);
        }
        return entity;
    }
}
