package in.springframework.learning.tutorial.configurations;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;

@Log4j2
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
    @Value("${spring.data.mongodb.write-concern}")
    private String writeConcern;
    @Value("${spring.config.activate.on-cloud-platform:}")
    private String cloudPlatform;
    @Value("${spring.config.activate.on-profile}")
    private String activeProfile;


    @SneakyThrows
    @Bean
    public MongoClient mongoClient() {

        if (!cloudPlatform.equals("kubernetes")) {

            return MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(getConnectionString()))
                    .credential(MongoCredential.createScramSha256Credential(username, database, URLEncoder.encode(password, "UTF-8").toCharArray()))
                    .readConcern(ReadConcern.MAJORITY)
                    .readPreference(ReadPreference.primaryPreferred())

                    .writeConcern(WriteConcern.class.cast(WriteConcern.class.getDeclaredField(writeConcern).get(null)))
                    .build()
            );
        }
        else {

            return MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(getConnectionString()))
                    .readConcern(ReadConcern.MAJORITY)
                    .readPreference(ReadPreference.primaryPreferred())

                    .writeConcern(WriteConcern.class.cast(WriteConcern.class.getDeclaredField(writeConcern).get(null)))
                    .build()
            );
        }
    }

    @Bean(name = "defaultMongoTemplate")
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), database);
    }
    private String getConnectionString() throws UnsupportedEncodingException {
        log.info(String.format("Spring profile is %s and cloud platform is %s", activeProfile, cloudPlatform));
        if (!cloudPlatform.equals("kubernetes")) {

            return String.format("mongodb://%s:%d", host, port);
        }
        else {

            return String.format("mongodb+srv://%s:%s@%s", username, URLEncoder.encode(password, "UTF-8"), host);
        }
    }
    @Override
    protected String getDatabaseName() {
        return database;
    }
}
