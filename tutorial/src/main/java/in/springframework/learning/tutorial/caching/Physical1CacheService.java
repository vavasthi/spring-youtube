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
import in.springframework.learning.tutorial.configurations.FirstRedisConfiguration;
import in.springframework.learning.tutorial.exceptions.CacheKeyAnnotationAbsent;
import in.springframework.learning.tutorial.exceptions.NoMatchingkeyCombinationsExist;
import in.springframework.learning.tutorial.pojos.CachedEntity;
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
public abstract class Physical1CacheService<I, E extends CachedEntity<I> > extends AbstractCacheService<I, E> {

    @Autowired
    private FirstRedisConfiguration redisConfiguration;

    protected <V> V getObject(Object key,
                              Class<V> vClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return vClass.cast(getObject(redisConfiguration.redisTemplate(), key));
    }
    protected void storeObject(Object key,
                               Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        storeObject(redisConfiguration.redisTemplate(), key, value);
    }
    protected Optional<E> evictObject(I id,
                                      Class<E> entityClass,
                                      Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return evictObject(redisConfiguration.redisTemplate(), id, entityClass, repositoryClass);
    }
    public Optional<E> findById(I id,
                                Class<E> entityClass,
                                Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return findById(redisConfiguration.redisTemplate(), id, entityClass, repositoryClass);
    }

    public Optional<E> delete(I id,
                              Class<E> entityClass,
                              Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return delete(redisConfiguration.redisTemplate(), id, entityClass, repositoryClass);
    }

    public Optional<E> create(E entity,
                              Class<E > entityClass,
                              Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return create(redisConfiguration.redisTemplate(), entity, entityClass, repositoryClass);
    }
    public Optional<E> update(I id,
                              E entity,
                              Class<E> entityClass,
                              Class<? extends CrudRepository> repositoryClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return update(redisConfiguration.redisTemplate(), id, entity, entityClass, repositoryClass);
    }
}