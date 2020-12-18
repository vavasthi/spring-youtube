package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.annotations.AuthenticatedUserOrAdmin;
import in.springframework.learning.tutorial.exceptions.BadRequestException;
import in.springframework.learning.tutorial.pojos.TutorialMessage;
import in.springframework.learning.tutorial.utils.TutorialConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/kafka")
public class TutorialTopicMessageSenderEndpoint {

    @Autowired
    @Qualifier(TutorialConstants.FIRST_TOPIC_TEMPLATE_NAME)
    private KafkaTemplate<UUID, TutorialMessage> firstTopicTemplate;

    @Autowired
    @Qualifier(TutorialConstants.SECOND_TOPIC_TEMPLATE_NAME)
    private KafkaTemplate<UUID, TutorialMessage> secondTopicTemplate;

    @RequestMapping(value = "/{topic}",
            method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthenticatedUserOrAdmin
    public TutorialMessage sendMessage(@PathVariable("topic") String topic,
                                       @RequestBody TutorialMessage message) {

        if (topic.equals(TutorialConstants.FIRST_TOPIC_NAME)) {
            firstTopicTemplate.send(topic, UUID.randomUUID(), message);
        }
        else if(topic.equals(TutorialConstants.SECOND_TOPIC_NAME)) {
            secondTopicTemplate.send(topic, UUID.randomUUID(), message);
        }
        else {
            throw new BadRequestException(String.format("%s is an invalid topic type"));
        }
        log.info("Message " + message.toString() + " sent successfully!");
        return message;
    }
}
