package in.springframework.learning.tutorial.serializers;

import com.google.gson.Gson;
import in.springframework.learning.tutorial.pojos.TutorialMessage;
import org.apache.kafka.common.serialization.Serializer;

public class TutorialMessageSerializer implements Serializer<TutorialMessage> {
    @Override
    public byte[] serialize(String topic, TutorialMessage tutorialMessage) {

        Gson gson = new Gson();
        tutorialMessage.setTopic(topic);
        return gson.toJson(tutorialMessage).getBytes();
    }
}
