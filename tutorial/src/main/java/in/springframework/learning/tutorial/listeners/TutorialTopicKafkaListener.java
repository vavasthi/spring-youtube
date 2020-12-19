package in.springframework.learning.tutorial.listeners;

import in.springframework.learning.tutorial.pojos.TutorialMessage;
import in.springframework.learning.tutorial.utils.TutorialConstants;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Log4j2
public class TutorialTopicKafkaListener {

    @KafkaListener(topics = TutorialConstants.FIRST_TOPIC_NAME,
            containerFactory = "firstTopicSecondGroupContainerFactory")
    public void processMessageFTSG(@Payload TutorialMessage message, Acknowledgment acknowledgment) {
        log.info(String.format("Message received by second group, topic = %s and message = %s",
                message.getTopic(),
                message.getMsg()));
        acknowledgment.acknowledge();
    }
    @KafkaListener(topics = TutorialConstants.SECOND_TOPIC_NAME,
            containerFactory = "secondTopicFirstGroupContainerFactory")
    public void processMessageSTFG(@Payload TutorialMessage message) {
        log.info(String.format("Message received by first group, topic = %s and message = %s",
                message.getTopic(),
                message.getMsg()));
    }
    @KafkaListener(topics = TutorialConstants.SECOND_TOPIC_NAME,
            containerFactory = "secondTopicSecondGroupContainerFactory")
    public void processMessageSTSG(@Payload TutorialMessage message) {
        log.info(String.format("Message received by second group, topic = %s and message = %s",
                message.getTopic(),
                message.getMsg()));
    }

    @KafkaListener(id="P02", topicPartitions = {
            @TopicPartition(topic = TutorialConstants.FIRST_TOPIC_NAME, partitions = {"0","2"})
    }, containerFactory = "firstTopicFirstGroupContainerFactory")
    public void processMessageFTFGP02(List<TutorialMessage> messages, Acknowledgment acknowledgment) {
        for (TutorialMessage m : messages) {

            log.info(String.format("P02 Message received by first group, topic = %s and message = %s",
                    m.getTopic(),
                    m.getMsg()));
        }
        acknowledgment.acknowledge();
    }
    @KafkaListener(id = "P13", topicPartitions = {
            @TopicPartition(topic = TutorialConstants.FIRST_TOPIC_NAME, partitions = {"1","3"})
    }, containerFactory = "firstTopicFirstGroupContainerFactory")
    public void processMessageFTFGP13(List<TutorialMessage> messages, Acknowledgment acknowledgment) {
        for (TutorialMessage m : messages) {

            log.info(String.format("P13 Message received by first group, topic = %s and message = %s",
                    m.getTopic(),
                    m.getMsg()));
        }
        acknowledgment.acknowledge();
    }

}
