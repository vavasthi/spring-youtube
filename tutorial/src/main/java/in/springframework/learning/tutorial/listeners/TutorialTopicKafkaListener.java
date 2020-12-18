package in.springframework.learning.tutorial.listeners;

import in.springframework.learning.tutorial.pojos.TutorialMessage;
import in.springframework.learning.tutorial.utils.TutorialConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TutorialTopicKafkaListener {

    @KafkaListener(topics = TutorialConstants.FIRST_TOPIC_NAME,
            containerFactory = "firstTopicFirstGroupContainerFactory")
    public void processMessageFTFG(@Payload TutorialMessage message) {
        log.info(String.format("Message received by first group, topic = %s and message = %s",
                message.getTopic(),
                message.getMsg()));
    }
    @KafkaListener(topics = TutorialConstants.FIRST_TOPIC_NAME,
            containerFactory = "firstTopicSecondGroupContainerFactory")
    public void processMessageFTSG(@Payload TutorialMessage message) {
        log.info(String.format("Message received by second group, topic = %s and message = %s",
                message.getTopic(),
                message.getMsg()));
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
}
