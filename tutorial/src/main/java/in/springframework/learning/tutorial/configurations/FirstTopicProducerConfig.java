package in.springframework.learning.tutorial.configurations;

import in.springframework.learning.tutorial.pojos.TutorialMessage;
import in.springframework.learning.tutorial.serializers.TutorialMessageSerializer;
import in.springframework.learning.tutorial.utils.TutorialConstants;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class FirstTopicProducerConfig {

    @Value("${spring.kafka.topic1.bootstrap-servers}")
    private String bootstrapServer;

    public ProducerFactory<UUID, TutorialMessage> producerFactory() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TutorialMessageSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProperties);
    }
    @Bean(name = TutorialConstants.FIRST_TOPIC_TEMPLATE_NAME)
    public KafkaTemplate<UUID, TutorialMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
