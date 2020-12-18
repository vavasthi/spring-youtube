package in.springframework.learning.tutorial.listeners;

import in.springframework.learning.tutorial.endpoints.TutorialTopicMessageSenderEndpoint;
import in.springframework.learning.tutorial.utils.TutorialConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TutorialTopicKafkaListener {

    @KafkaListener(topics = TutorialConstants.TUTORIAL_TOPIC)
    public void processMessage(String content) {
        log.info("Received Message" + content);

    }
}
