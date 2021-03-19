package in.springframework.learning.tutorial.caching;

import in.springframework.learning.tutorial.annotations.DefineCache;
import in.springframework.learning.tutorial.annotations.RelatedCache;
import in.springframework.learning.tutorial.annotations.RelatedCaches;
import in.springframework.learning.tutorial.exceptions.EntityDoesnotExist;
import in.springframework.learning.tutorial.pojos.CachedEntity;
import in.springframework.learning.tutorial.pojos.Contained1Entity;
import in.springframework.learning.tutorial.pojos.ContainerEntity;
import in.springframework.learning.tutorial.repositories.Contained1EntityRepository;
import in.springframework.learning.tutorial.repositories.ContainerEntityRepository;
import in.springframework.learning.tutorial.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@DefineCache(name = Constants.CONTAINED_1_CACHE_NAME,
        prefix = Constants.CONTAINED_1_CACHE_PREFIX,
        expiry = Constants.CONTAINED_1_CACHE_EXPIRY)
/*@RelatedCaches(name = "contained1",
        caches = {
                @RelatedCache(primaryKeyField = "containerId", clazz = ContainerCacheService.class)
        })*/
@Service
public class Contained1CacheService extends Physical1CacheService<String, Contained1Entity> {
    @Autowired
    private Contained1EntityRepository repository;
    @Autowired
    private ContainerEntityRepository containerEntityRepository;
    @Override
    public Optional<Contained1Entity> findById(String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return findById(id, Contained1Entity.class, Contained1EntityRepository.class);
    }

    @Override
    public Iterable<Contained1Entity> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Contained1Entity> evict(String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return evictObject(id, Contained1Entity.class, Contained1EntityRepository.class);
    }

    @Override
    public Iterable<KeyPrefixForCache> getAllKeys(String id, List<KeyPrefixForCache> keys)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return getAllKeys(id, keys, Contained1Entity.class, Contained1EntityRepository.class);
    }

    @Override
    public Optional<Contained1Entity> delete(String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Optional<Contained1Entity> optionalContained1Entity = repository.findById(id);
        if (optionalContained1Entity.isPresent()) {

            Contained1Entity entity = optionalContained1Entity.get();
            Optional<ContainerEntity> optionalContainerEntity =
                    containerEntityRepository.findById(entity.getContainerId());
            if (optionalContainerEntity.isPresent()) {
                ContainerEntity containerEntity = optionalContainerEntity.get();
                containerEntity.getContained1().remove(entity.getId());
                containerEntityRepository.save(containerEntity);
                return delete(id, Contained1Entity.class, Contained1EntityRepository.class);
            }
        }
        throw new EntityDoesnotExist(String.format("Contained1 %s doesn't exist", id));
    }

    @Override
    public Optional<Contained1Entity> create(Contained1Entity entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Optional<ContainerEntity> optionalContainerEntity =
                containerEntityRepository.findById(entity.getContainerId());
        if (optionalContainerEntity.isPresent()) {
            ContainerEntity containerEntity = optionalContainerEntity.get();
            if (containerEntity.getContained1() == null) {
                containerEntity.setContained1(new HashSet<>());
            }
            Optional<Contained1Entity> optionalContained1Entity
                    = create(entity, Contained1Entity.class, Contained1EntityRepository.class);
            containerEntity.getContained1().add(optionalContained1Entity.get().getId());
            containerEntityRepository.save(containerEntity);
            return optionalContained1Entity;
        }
        throw new EntityDoesnotExist(String.format("Container %s doesn't exist.", entity.getContainerId()));
    }
    @Override
    public Optional<Contained1Entity> update(String id,
                                             Contained1Entity entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return update(id, entity, Contained1Entity.class, Contained1EntityRepository.class);
    }
}
