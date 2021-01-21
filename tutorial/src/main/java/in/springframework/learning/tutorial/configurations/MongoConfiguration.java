package in.springframework.learning.tutorial.configurations;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.kafka.common.security.auth.Login;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.MongoDbFactoryParser;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "in.springframework.learning.tutorial.repositories",
        mongoTemplateRef = "defaultMongoTemplate")
public class MongoConfiguration extends   AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String database;
    @Value("${spring.data.mongodb.host}")
    private String host;
    @Value("${spring.data.mongodb.port}")
    private Integer port;
    @Value("${spring.data.mongodb.username}")
    private String username;
    @Value("${spring.data.mongodb.password}")
    private String password;


    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(getConnectionString()))
                .credential(MongoCredential.createCredential(username, database, password.toCharArray()))
                .readConcern(ReadConcern.MAJORITY)
                .readPreference(ReadPreference.primaryPreferred())
//                .writeConcern(WriteConcern.W2)
                .build()
        );
    }

    @Bean(name = "defaultMongoTemplate")
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), database);
    }
    private String getConnectionString() {
        return String.format("mongodb://%s:%d", host, port);
    }
    @Override
    protected String getDatabaseName() {
        return database;
    }
}
