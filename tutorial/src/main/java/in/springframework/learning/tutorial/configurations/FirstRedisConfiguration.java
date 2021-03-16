package in.springframework.learning.tutorial.configurations;

import in.springframework.learning.tutorial.caching.KeyPrefixForCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;


@Configuration
public class FirstRedisConfiguration {

    private
    @Value("${redis.first.nodes:localhost}")
    String redisHost;
    @Value("${redis.first.database:1}")
    private int redisDatabase;
    private
    @Value("${redis.first.password:null}")
    String redisPassword;
    @Value("${redis.first.pool.maxIdle:5}")
    private int maxIdle;
    private
    @Value("${redis.first.pool.minIdle:1}")
    int minIdle;
    private
    @Value("${redis.first.pool.maxRedirects:3}")
    int maxRedirects;
    private
    @Value("${redis.first.pool.maxTotal:20}")
    int maxTotal;
    private
    @Value("${redis.first.pool.maxWaitMillis:3000}")
    int maxWaitMillis;

    JedisConnectionFactory jedisSchedulerConnectionFactory() {

        String[] hosts = redisHost.split(",");
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setMaxTotal(maxTotal);

        if (hosts.length == 1) {
            return new JedisConnectionFactory(poolConfig);
        }
        else {

            RedisClusterConfiguration configuration = new RedisClusterConfiguration(Arrays.asList(hosts));
            if (redisPassword != null || !redisPassword.isEmpty()) {
                configuration.setPassword(RedisPassword.of(redisPassword));
            }
            configuration.setMaxRedirects(maxRedirects);
            JedisConnectionFactory factory = new JedisConnectionFactory(configuration, poolConfig);
            return factory;
        }
    }

    public RedisTemplate<KeyPrefixForCache, Object> redisTemplate() {
        RedisTemplate<KeyPrefixForCache, Object> redisTemplate = new RedisTemplate<KeyPrefixForCache, Object>();
        redisTemplate.setConnectionFactory(jedisSchedulerConnectionFactory());
        redisTemplate.setKeySerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}