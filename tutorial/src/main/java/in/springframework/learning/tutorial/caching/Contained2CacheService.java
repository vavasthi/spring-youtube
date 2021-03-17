package in.springframework.learning.tutorial.caching;

import in.springframework.learning.tutorial.annotations.DefineCache;
import in.springframework.learning.tutorial.annotations.RelatedCache;
import in.springframework.learning.tutorial.annotations.RelatedCaches;
import in.springframework.learning.tutorial.configurations.FirstRedisConfiguration;
import in.springframework.learning.tutorial.exceptions.EntityDoesnotExist;
import in.springframework.learning.tutorial.pojos.CachedEntity;
import in.springframework.learning.tutorial.pojos.Contained1Entity;
import in.springframework.learning.tutorial.pojos.Contained2Entity;
import in.springframework.learning.tutorial.pojos.ContainerEntity;
import in.springframework.learning.tutorial.repositories.Contained1EntityRepository;
import in.springframework.learning.tutorial.repositories.Contained2EntityRepository;
import in.springframework.learning.tutorial.repositories.ContainerEntityRepository;
import in.springframework.learning.tutorial.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@DefineCache(name = Constants.CONTAINED_2_CACHE_NAME,
        prefix = Constants.CONTAINED_2_CACHE_PREFIX,
        expiry = Constants.CONTAINED_2_CACHE_EXPIRY)
@RelatedCaches(name = "contained2",
        caches = {
                @RelatedCache(primaryKeyField = "containerId", clazz = ContainerCacheService.class)
        })
@Service
public class Contained2CacheService extends Physical1CacheService<String, Contained2Entity> {

    @Autowired
    private Contained2EntityRepository repository;
    @Autowired
    private ContainerEntityRepository containerEntityRepository;

    @Override
    public Optional<Contained2Entity> findById(String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return findById(id, Contained2Entity.class, Contained2EntityRepository.class);
    }

    @Override
    public Iterable<Contained2Entity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Contained2Entity> evict(String id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return evictObject(id, Contained2Entity.class, Contained2EntityRepository.class);
    }
    @Override
    public Optional<Contained2Entity> delete(String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Optional<Contained2Entity> optionalContained2Entity = repository.findById(id);
        if (optionalContained2Entity.isPresent()) {

            Contained2Entity entity = optionalContained2Entity.get();
            Optional<ContainerEntity> optionalContainerEntity =
                    containerEntityRepository.findById(entity.getContainerId());
            if (optionalContainerEntity.isPresent()) {
                ContainerEntity containerEntity = optionalContainerEntity.get();
                containerEntity.getContained2().remove(entity.getId());
                containerEntityRepository.save(containerEntity);
                return delete(id, Contained2Entity.class, Contained2EntityRepository.class);
            }
        }
        throw new EntityDoesnotExist(String.format("Contained2 %s doesn't exist", id));
    }

    @Override
    public Optional<Contained2Entity> create(Contained2Entity entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Optional<ContainerEntity> optionalContainerEntity =
                containerEntityRepository.findById(entity.getContainerId());
        if (optionalContainerEntity.isPresent()) {
            ContainerEntity containerEntity = optionalContainerEntity.get();
            if (containerEntity.getContained2() == null) {
                containerEntity.setContained2(new HashSet<>());
            }
            Optional<Contained2Entity> optionalContained2Entity
                    = create(entity, Contained2Entity.class, Contained2EntityRepository.class);
            containerEntity.getContained2().add(optionalContained2Entity.get().getId());
            containerEntityRepository.save(containerEntity);
            return optionalContained2Entity;
        }
        throw new EntityDoesnotExist(String.format("Container %s doesn't exist.", entity.getContainerId()));
    }
    @Override
    public Optional<Contained2Entity> update(String id,
                                             Contained2Entity entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return update(id, entity, Contained2Entity.class, Contained2EntityRepository.class);
    }
}
