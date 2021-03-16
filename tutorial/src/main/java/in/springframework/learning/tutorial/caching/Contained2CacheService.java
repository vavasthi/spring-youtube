package in.springframework.learning.tutorial.caching;

import in.springframework.learning.tutorial.annotations.DefineCache;
import in.springframework.learning.tutorial.configurations.FirstRedisConfiguration;
import in.springframework.learning.tutorial.pojos.Contained1Entity;
import in.springframework.learning.tutorial.pojos.Contained2Entity;
import in.springframework.learning.tutorial.repositories.Contained1EntityRepository;
import in.springframework.learning.tutorial.repositories.Contained2EntityRepository;
import in.springframework.learning.tutorial.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@DefineCache(name = Constants.CONTAINED_2_CACHE_NAME,
        prefix = Constants.CONTAINED_2_CACHE_PREFIX,
        expiry = Constants.CONTAINED_2_CACHE_EXPIRY)
@Service
public class Contained2CacheService extends Physical1CacheService<String, Contained2Entity> {

    @Autowired
    private Contained2EntityRepository repository;

    @Override
    protected Contained2Entity findById(String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try {
            return getObject(id, Contained2Entity.class);
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        Optional<Contained2Entity> optionalEntity = repository.findById(id);
        if (optionalEntity.isPresent()) {
            Contained2Entity entity = optionalEntity.get();
            storeObject(entity.getId(), entity);
            return entity;
        }
        return null;
    }
    @Override
    protected Contained2Entity evict(String id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Contained2Entity entity = findById(id);
        if (entity != null) {

            evictObject(id, entity);
        }
        return entity;
    }
}
