package in.springframework.learning.tutorial.configurations;

import in.springframework.learning.tutorial.deserializer.TutorialMessageDeserializer;
import in.springframework.learning.tutorial.pojos.TutorialMessage;
import in.springframework.learning.tutorial.serializers.TutorialMessageSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class SecondTopicConsumerConfig {


    @Value("${spring.kafka.topic2.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.topic2.consumer.group-id1}")
    private String consumerGroup1;
    @Value("${spring.kafka.topic2.consumer.group-id2}")
    private String consumerGroup2;


    public ConsumerFactory<UUID, TutorialMessage> consumerFactory(String consumerGroupId) {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TutorialMessageDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configProperties);
    }

    @Bean(name = "secondTopicFirstGroupContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<UUID, TutorialMessage>
            kafkaFirstGroupListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<UUID, TutorialMessage> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(consumerGroup1));
        return factory;
    }
    @Bean(name = "secondTopicSecondGroupContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<UUID, TutorialMessage>
    kafkaSecondGroupListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<UUID, TutorialMessage> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(consumerGroup2));
        return factory;
    }
}
