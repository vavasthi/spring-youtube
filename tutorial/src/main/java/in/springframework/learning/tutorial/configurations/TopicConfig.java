package in.springframework.learning.tutorial.configurations;

import in.springframework.learning.tutorial.utils.TutorialConstants;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TopicConfig {

    @Value("${spring.kafka.topic1.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.topic1.no-partitions:3}")
    private int noPartitionsForFirstTopic;
    @Value("${spring.kafka.topic2.no-partitions:3}")
    private int noPartitionsForSecondTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return new KafkaAdmin(configProperties);
    }
    @Bean
    public NewTopic firstTopic() {
        return TopicBuilder
                .name(TutorialConstants.FIRST_TOPIC_NAME)
                .partitions(noPartitionsForFirstTopic)
                .build();
    }
    @Bean
    public NewTopic secondTopic() {
        return TopicBuilder
                .name(TutorialConstants.SECOND_TOPIC_NAME)
                .partitions(noPartitionsForSecondTopic)
                .build();
    }
}
