package in.springframework.learning.tutorial.pojos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TutorialMessage {

    private String topic;
    private String msg;
    private String key;
}
