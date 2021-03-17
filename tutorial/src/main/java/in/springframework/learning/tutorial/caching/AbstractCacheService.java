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
import in.springframework.learning.tutorial.pojos.Contained1Entity;
import in.springframework.learning.tutorial.repositories.Contained1EntityRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public abstract class AbstractCacheService<I, E> {

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
    abstract public Optional<E> delete(I id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
    abstract public Optional<E> create(E entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;
    abstract public Optional<E> update(I id, E entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    protected String getPrefix() {
        return getClass().getAnnotation(DefineCache.class).prefix();
    }


    protected void storeObject(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                               Object key,
                               Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {


        CacheKeys cacheKeys = getClass().getAnnotation(CacheKeys.class);
        if (cacheKeys == null) {

            redisTemplate.opsForValue().set(new KeyPrefixForCache(getPrefix(), key), value);
        } else {
            if (cacheKeys.keys().length > 0) {
                Map<KeyPrefixForCache, Object> keyValueMap = new HashMap<>();
                keyValueMap.put(new KeyPrefixForCache(getPrefix(), key), value);
                for (CacheKey ck : cacheKeys.keys()) {
                    keyValueMap.put(getKey(ck, value), key);
                }
                redisTemplate.opsForValue().multiSet(keyValueMap);
                for (KeyPrefixForCache kpfc : keyValueMap.keySet()) {

                    redisTemplate.expire(kpfc, getExpiry(), TimeUnit.SECONDS);
                }
            }
        }
        evictRelatedCacheObject(key, value);
    }
    private void evictRelatedCacheObject(Object key, Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        RelatedCaches relatedCaches = getClass().getAnnotation(RelatedCaches.class);
        if (relatedCaches != null) {

            for (RelatedCache relatedCache : relatedCaches.caches()) {
                AbstractCacheService abstractCacheService
                        = applicationContext.getBean(relatedCache.clazz());
                Method m = getGetterMethod(value, relatedCache.primaryKeyField());
                abstractCacheService.evict(m.invoke(value));
            }
        }
    }
    protected Optional<E> evictObject(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                      I id,
                                      Class<E> entityClass,
                                      Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {


        CacheKeys cacheKeys = getClass().getAnnotation(CacheKeys.class);
        KeyPrefixForCache keyPrefixForCache = new KeyPrefixForCache(getPrefix(), id);
        Optional<E> optionalEntity = findById(redisTemplate, id, entityClass, repositoryClass);
        E entity = null;
        if (optionalEntity.isPresent()) {
            entity = optionalEntity.get();
        }
        if (cacheKeys == null) {

            redisTemplate.delete(keyPrefixForCache);
        } else {
            if (cacheKeys.keys().length > 0 && optionalEntity.isPresent()) {
                List<KeyPrefixForCache> keys = new ArrayList<>();
                keys.add(keyPrefixForCache);
                for (CacheKey ck : cacheKeys.keys()) {
                    keys.add(getKey(ck, entity));
                }
                redisTemplate.delete(keys);
            }
        }
        evictRelatedCacheObject(id, entity);
        return optionalEntity;
    }

    protected Optional<E> delete(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                 I id,
                                 Class<E> entityClass,
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
                                 Class<E> entityClass,
                                 Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        entity = entityClass.cast(applicationContext.getBean(repositoryClass).save(entity));
        return Optional.of(entity);
    }
    protected Optional<E> update(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                 I id,
                                 E entity,
                                 Class<E> entityClass,
                                 Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        entity = entityClass.cast(applicationContext.getBean(repositoryClass).save(entity));
        return Optional.of(entity);
    }

    protected Optional<E> findById(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                                             I id,
                                             Class<E> entityClass,
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
    protected <V> V getObject(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                          Object key,
                          Class<V> vClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return vClass.cast(getObject(redisTemplate, key));
    }

    protected <V> V getObject(RedisTemplate<KeyPrefixForCache, Object> redisTemplate,
                          Map<String, Object> keys,
                          Class<V> vClass)
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