package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.annotations.AuthenticatedUserOrAdmin;
import in.springframework.learning.tutorial.pojos.TutorialMessage;
import in.springframework.learning.tutorial.utils.TutorialConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/kafka")
public class TutorialTopicMessageSenderEndpoint {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthenticatedUserOrAdmin
    public TutorialMessage sendMessage(@RequestBody TutorialMessage message) {
        kafkaTemplate.send(TutorialConstants.TUTORIAL_TOPIC, message.toString());
        log.info("Message " + message.toString() + " sent successfully!");
        return message;
    }
}
