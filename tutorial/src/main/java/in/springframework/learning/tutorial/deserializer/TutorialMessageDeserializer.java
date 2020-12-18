package in.springframework.learning.tutorial.deserializer;

import com.google.gson.Gson;
import in.springframework.learning.tutorial.pojos.TutorialMessage;
import org.apache.kafka.common.serialization.Deserializer;

public class TutorialMessageDeserializer implements Deserializer<TutorialMessage> {
    @Override
    public TutorialMessage deserialize(String topic, byte[] bytes) {

        Gson gson = new Gson();
        TutorialMessage tutorialMessage
                = gson.fromJson(new String(bytes), TutorialMessage.class);
        tutorialMessage.setTopic(topic);
        return tutorialMessage;
    }
}
