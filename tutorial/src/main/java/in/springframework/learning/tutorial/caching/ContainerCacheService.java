package in.springframework.learning.tutorial.caching;

import in.springframework.learning.tutorial.annotations.DefineCache;
import in.springframework.learning.tutorial.pojos.ContainerEntity;
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
    public Optional<ContainerEntity> update(String id,
                                            ContainerEntity entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return update(id, entity, ContainerEntity.class, ContainerEntityRepository.class);
    }
}
