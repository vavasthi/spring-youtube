package in.springframework.learning.tutorial.caching;

import in.springframework.learning.tutorial.annotations.DefineCache;
import in.springframework.learning.tutorial.configurations.FirstRedisConfiguration;
import in.springframework.learning.tutorial.pojos.Contained1Entity;
import in.springframework.learning.tutorial.pojos.Contained2Entity;
import in.springframework.learning.tutorial.pojos.ContainerEntity;
import in.springframework.learning.tutorial.repositories.Contained1EntityRepository;
import in.springframework.learning.tutorial.repositories.ContainerEntityRepository;
import in.springframework.learning.tutorial.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@DefineCache(name = Constants.CONTAINER_CACHE_NAME,
        prefix = Constants.CONTAINER_CACHE_PREFIX,
        expiry = Constants.CONTAINER_CACHE_EXPIRY)
@Service
public class ContainerCacheService extends Physical1CacheService<String, ContainerEntity> {
    @Autowired
    private ContainerEntityRepository repository;

    @Override
    protected ContainerEntity findById(String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try {
            return getObject(id, ContainerEntity.class);
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        Optional<ContainerEntity> optionalEntity = repository.findById(id);
        if (optionalEntity.isPresent()) {
            ContainerEntity entity = optionalEntity.get();
            storeObject(entity.getId(), entity);
            return entity;
        }
        return null;
    }
    @Override
    protected ContainerEntity evict(String id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ContainerEntity entity = findById(id);
        if (entity != null) {

            evictObject(id, entity);
        }
        return entity;
    }
}
