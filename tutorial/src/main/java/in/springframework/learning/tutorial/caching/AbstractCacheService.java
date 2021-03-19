/*
 * Copyright (c) 2021 Vinay Avasthi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.springframework.learning.tutorial.caching;

import in.springframework.learning.tutorial.annotations.*;
import in.springframework.learning.tutorial.exceptions.CacheKeyAnnotationAbsent;
import in.springframework.learning.tutorial.exceptions.NoMatchingkeyCombinationsExist;
import in.springframework.learning.tutorial.pojos.CachedEntity;
import in.springframework.learning.tutorial.pojos.ClassAttributePair;
import in.springframework.learning.tutorial.utils.CacheCascadeAnnotationDictionary;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public abstract class AbstractCacheService<I, E extends CachedEntity<I> > {

    @Autowired
    private ApplicationContext applicationContext;

    private static final Logger logger = LogManager.getLogger(AbstractCacheService.class);
    private static final String PRIMARY_KEY_KEY = "KEY_FOR_PRIMARY_KEY";

    protected long getExpiry() {

        return this.getClass().getAnnotation(DefineCache.class).expiry();
    }
    abstract public Optional<E> findById(I id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
    abstract public Iterable<E> findAll();
    abstract public Optional<E> evict(I id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
    abstract public Iterable<KeyPrefixForCache> getAllKeys(I id, List<KeyPrefixForCache> keys)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
    abstract public Optional<E> delete(I id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
    abstract public Optional<E> create(E entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
    abstract public Optional<E> update(I id, E entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    protected String getPrefix() {
        return getClass().getAnnotation(DefineCache.class).prefix();
    }

    @PostConstruct
    private void postConstruct() {
        CachesCascade cachesCascade = getClass().getDeclaredAnnotation(CachesCascade.class);
        if (cachesCascade != null && cachesCascade.caches() != null) {
            for (CacheCascade cc : cachesCascade.caches()) {
                CacheCascadeAnnotationDictionary.INSTANCE.add(cc.attribute(), getClass(), cc.clazz());
            }
        }
    }

    protected void storeObject(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                               Object key,
                               Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {


        Map<KeyPrefixForCache, Object> keyValueMap = new HashMap<>();
        Map<KeyPrefixForCache, Long> timeoutMap = new HashMap<>();
        Map<KeyPrefixForCache, Set<Object>> setValueMap = new HashMap<>();
        CacheKeys cacheKeys = getClass().getAnnotation(CacheKeys.class);
        keyValueMap.put(new KeyPrefixForCache(getPrefix(), key), value);
        timeoutMap.put(new KeyPrefixForCache(getPrefix(), key), getExpiry());
        if (cacheKeys != null && cacheKeys.keys().length > 0) {
            for (CacheKey ck : cacheKeys.keys()) {
                KeyPrefixForCache kpfc = getKey(ck, value);
                keyValueMap.put(kpfc, key);
                timeoutMap.put(kpfc, getExpiry());
            }
        }
        Set<ClassAttributePair> annotationSet =
                CacheCascadeAnnotationDictionary.INSTANCE.getAnnotationsOnAnnotatedClass(getClass());
        if (annotationSet != null) {

            for (ClassAttributePair s : annotationSet) {

                Method m = getGetterMethod(value, s.getAttribute());
                Collection collection = (Collection)m.invoke(value);
                for (Object v : collection) {
                    KeyPrefixForCache kpfc = new KeyPrefixForCache(getPrefix(), (I)v);
                    Set<Object> valueSet = setValueMap.get(kpfc);
                    if (valueSet == null) {
                        valueSet = new HashSet<>();
                        setValueMap.put(kpfc, valueSet);
                        timeoutMap.put(kpfc, getExpiry());
                    }
                    valueSet.add(key);
                }
            }
        }
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                redisOperations.multi();
                for (Map.Entry<KeyPrefixForCache, Object> e : keyValueMap.entrySet()) {
                    redisOperations.opsForValue().set((K)e.getKey(), (V)e.getValue());
                }
                for (Map.Entry<KeyPrefixForCache, Set<Object> > e : setValueMap.entrySet()) {
                    for (Object v : e.getValue()) {

                        redisOperations.opsForSet().add((K)e.getKey(), (V)v);
                    }
                }
                for (Map.Entry<KeyPrefixForCache, Long> e : timeoutMap.entrySet()) {
                    redisOperations.expire((K)e.getKey(), e.getValue(), TimeUnit.SECONDS);
                }
                return redisOperations.exec();
            }
        });
    }

    protected Optional<E> evictObject(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                      I id,
                                      Class<E> entityClass,
                                      Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Optional<E> optionalEntity = findById(id);
        List<KeyPrefixForCache> keys = new ArrayList<>();
        getAllKeys(redisTemplate, id, keys, entityClass, repositoryClass);
        redisTemplate.delete(keys);
        return optionalEntity;
    }
    protected Iterable<KeyPrefixForCache> getAllKeys(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                                     I id,
                                                     List<KeyPrefixForCache> keys,
                                                     Class<E > entityClass,
                                                     Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {


        CacheKeys cacheKeys = getClass().getAnnotation(CacheKeys.class);
        KeyPrefixForCache keyPrefixForCache = new KeyPrefixForCache(getPrefix(), id);
        Optional<E> optionalEntity = findById(redisTemplate, id, entityClass, repositoryClass);
        E entity = null;
        if (optionalEntity.isPresent()) {
            entity = optionalEntity.get();
            keys.add(keyPrefixForCache);
            if (cacheKeys != null) {
                if (cacheKeys.keys().length > 0 && optionalEntity.isPresent()) {
                    for (CacheKey ck : cacheKeys.keys()) {
                        keys.add(getKey(ck, entity));
                    }
                }
            }
            Set<ClassAttributePair> annotationSet =
                    CacheCascadeAnnotationDictionary.INSTANCE.getAnnotationsOnAnnotatedClass(getClass());
            if (annotationSet != null) {

                for (ClassAttributePair cap : annotationSet) {

                    Method m = getGetterMethod(entity, cap.getAttribute());
                    Collection collection = (Collection)m.invoke(entity);
                    for (Object v : collection) {
                        keys.add(new KeyPrefixForCache(getPrefix(), (I)v));
                    }
                }
            }
        }

        getAllRelatedKeys(redisTemplate, entity, keys);
        return keys;
    }

    private Collection<KeyPrefixForCache> getAllRelatedKeys(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                                            E value,
                                                            List<KeyPrefixForCache> keys)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        RelatedCaches relatedCaches = getClass().getAnnotation(RelatedCaches.class);
        if (relatedCaches != null) {

            for (RelatedCache relatedCache : relatedCaches.caches()) {
                AbstractCacheService abstractCacheService
                        = applicationContext.getBean(relatedCache.clazz());
                Method m = getGetterMethod(value, relatedCache.primaryKeyField());
                I relatedId = (I)m.invoke(value);

                abstractCacheService.getAllKeys(relatedId, keys);
            }
        }
        Set<ClassAttributePair> targetAnnotationSet =
                CacheCascadeAnnotationDictionary.INSTANCE.getAnnotationsOnTargetClass(getClass());
        if (targetAnnotationSet != null) {

            for (ClassAttributePair cap : targetAnnotationSet) {

                Method m = getGetterMethod(value, "id");
                I id = (I)m.invoke(value);
                AbstractCacheService applicationCacheService = applicationContext.getBean(cap.getClazz());
                Set<Object> idsi = redisTemplate
                        .opsForSet()
                        .members(new KeyPrefixForCache(applicationCacheService.getPrefix(), id));
                if (idsi != null) {
                    for (Object k : idsi){

                        applicationCacheService.getAllKeys(k, keys);
                    }
                }
            }
        }
        return keys;
    }

    protected Optional<E > delete(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                  I id,
                                  Class<E > entityClass,
                                  Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Optional<E> optionalEntity = findById(redisTemplate, id, entityClass, repositoryClass);
        CrudRepository crudRepository = applicationContext.getBean(repositoryClass);
        crudRepository.deleteById(id);
        evictObject(redisTemplate, id, entityClass, repositoryClass);
        return optionalEntity;
    }

    protected Optional<E> create(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                 E entity,
                                 Class<E > entityClass,
                                 Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        entity = entityClass.cast(applicationContext.getBean(repositoryClass).save(entity));
        evictObject(redisTemplate, entity.getId(), entityClass, repositoryClass);
        return Optional.of(entity);
    }
    protected Optional<E> update(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                 I id,
                                 E entity,
                                 Class<E > entityClass,
                                 Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        entity = entityClass.cast(applicationContext.getBean(repositoryClass).save(entity));
        evictObject(redisTemplate, entity.getId(), entityClass, repositoryClass);
        return Optional.of(entity);
    }

    protected Optional<E > findById(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                    I id,
                                    Class<E > entityClass,
                                    Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try {
            E entity = getObject(redisTemplate, id, entityClass);
            if (entity != null) {
                return Optional.of(entity);
            }
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        Optional<E> optionalEntity
                = applicationContext.getBean(repositoryClass).findById(id);
        if (optionalEntity.isPresent()) {
            E entity = optionalEntity.get();
            storeObject(redisTemplate, id, entity);
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    /**
     * This function needs to be used when an object is stored against its primary key and that key is well known.
     * @param redisTemplate redisTemplate to be used
     * @param key - Key that is passed along.
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected Object getObject(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                               Object key)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return redisTemplate.opsForValue().get(new KeyPrefixForCache(getPrefix(), key));
    }
    protected  E getObject(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                           Object key,
                           Class<E> vClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return vClass.cast(getObject(redisTemplate, key));
    }

    protected E getObject(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                          Map<String, Object> keys,
                          Class<E > vClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        List<Object> orderedKey = new ArrayList<>();
        List<Object> orderedValue = new ArrayList<>();
        CacheKeys cacheKeys = getClass().getAnnotation(CacheKeys.class);
        if (cacheKeys == null)       {
            throw new CacheKeyAnnotationAbsent("@CacheKey annotation is absent from cache class.");
        }
        /** Since keys are passed as part of a map, this can't be the primary key. Here we have key value pairs
         * for multiple keys and we need to look for that specific key to find our primary key.
         */
        KeyPrefixForCache keyPrefixForCache = getPrimaryKey(redisTemplate, keys);
        // Once we get a valid primary key, we can lookup actual object stored against that primary key and
        // return.
        return getObject(redisTemplate, keyPrefixForCache, vClass);
    }

    private KeyPrefixForCache getPrimaryKey(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                            Map<String, Object> keys)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CacheKeys cacheKeys = getClass().getAnnotation(CacheKeys.class);
        if (cacheKeys == null)       {
            throw new CacheKeyAnnotationAbsent("@CacheKey annotation is absent from cache class.");
        }
        List<Object> orderedKey = new ArrayList<>();
        List<Object> orderedValue = new ArrayList<>();
        for (CacheKey ck : cacheKeys.keys()) {
            for (String f : ck.fields()) {
                Object sv  = keys.get(f);
                if (sv != null) {
                    orderedKey.add(f);
                    orderedValue.add(sv);
                }
            }
            if (orderedValue.size() == ck.fields().length) {
                /* This key matches the required key. */
                KeyPrefixForCache primaryKey = getKey(ck, orderedValue);
                return KeyPrefixForCache.class.cast(getObject(redisTemplate, primaryKey));
            }
        }
        throw new NoMatchingkeyCombinationsExist(String.format("Cache is not indexd for ollowing key combination (%s) doesn't exist.",
                StringUtils.join(keys.keySet())));
    }

    private KeyPrefixForCache getKey(CacheKey ck, List<Object> orderedValue) {

        switch(ck.fields().length) {
            case 1:
                return  new KeyPrefixForCache(getPrefix(), new KeyCacheSingleValue(orderedValue.get(0)
                ));
            case 2:
                return new KeyPrefixForCache(getPrefix(),
                        new KeyCacheDoubleValue(orderedValue.get(0),
                                orderedValue.get(1)
                        ));
            case 3:

                return  new KeyPrefixForCache(getPrefix(),
                        new KeyCacheTripleValue(orderedValue.get(0),
                                orderedValue.get(1),
                                orderedValue.get(2)
                        ));
            case 4:
                return new KeyPrefixForCache(getPrefix(),
                        new KeyCacheQuadValue(orderedValue.get(0),
                                orderedValue.get(1),
                                orderedValue.get(2),
                                orderedValue.get(3)
                        ));
            default:
            {
                List<Object> objectList = new ArrayList<>();
                for (int i = 4; i < orderedValue.size(); ++i) {
                    objectList.add(orderedValue.get(i));
                }
                return new KeyPrefixForCache(getPrefix(),
                        new KeyCacheAllValue(orderedValue.get(0),
                                orderedValue.get(1),
                                orderedValue.get(2),
                                orderedValue.get(3),
                                objectList
                        ));
            }
        }
    }
    private KeyPrefixForCache getKey(CacheKey cacheKey, Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<Object> values = new ArrayList<>();
        for (String f : cacheKey.fields()) {
            values.add(getGetterMethod(value, f).invoke(value));
        }
        return getKey(cacheKey, values);
    }
    private Method getGetterMethod(Object value, String fieldName) throws NoSuchMethodException {

        String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        if (!Character.isLowerCase(fieldName.charAt(0))) {
            methodName = "get" + fieldName.substring(1);
        }
        return value.getClass().getMethod(methodName);
    }
}