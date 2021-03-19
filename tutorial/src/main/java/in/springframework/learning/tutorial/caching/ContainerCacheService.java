package in.springframework.learning.tutorial.caching;

import in.springframework.learning.tutorial.annotations.CacheCascade;
import in.springframework.learning.tutorial.annotations.CachesCascade;
import in.springframework.learning.tutorial.annotations.DefineCache;
import in.springframework.learning.tutorial.pojos.Contained1Entity;
import in.springframework.learning.tutorial.pojos.ContainerEntity;
import in.springframework.learning.tutorial.repositories.Contained1EntityRepository;
import in.springframework.learning.tutorial.repositories.ContainerEntityRepository;
import in.springframework.learning.tutorial.utils.CacheCascadeAnnotationDictionary;
import in.springframework.learning.tutorial.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

@DefineCache(name = Constants.CONTAINER_CACHE_NAME,
        prefix = Constants.CONTAINER_CACHE_PREFIX,
        expiry = Constants.CONTAINER_CACHE_EXPIRY)
@CachesCascade(name = "containerToContained",
        caches = {
                @CacheCascade(attribute = "contained1", clazz = Contained1CacheService.class),
                @CacheCascade(attribute = "contained2", clazz = Contained2CacheService.class)
        })
@Service
public class ContainerCacheService extends Physical1CacheService<String, ContainerEntity> {
    @Autowired
    private ContainerEntityRepository repository;

    @Override
    public Optional<ContainerEntity> findById(String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return findById(id, ContainerEntity.class, ContainerEntityRepository.class);
    }
    @Override
    public Iterable<ContainerEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<ContainerEntity> evict(String id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return evictObject(id, ContainerEntity.class, ContainerEntityRepository.class);
    }
    @Override
    public Optional<ContainerEntity> delete(String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return delete(id, ContainerEntity.class, ContainerEntityRepository.class);
    }

    @Override
    public Optional<ContainerEntity> create(ContainerEntity entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return create(entity, ContainerEntity.class, ContainerEntityRepository.class);
    }
    @Override
    public Iterable<KeyPrefixForCache> getAllKeys(String id, List<KeyPrefixForCache> keys)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return getAllKeys(id, keys, ContainerEntity.class, ContainerEntityRepository.class);
    }

    @Override
    public Optional<ContainerEntity> update(String id,
                                            ContainerEntity entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return update(id, entity, ContainerEntity.class, ContainerEntityRepository.class);
    }
}
