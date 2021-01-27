package in.springframework.learning.tutorial.pojos;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Id;
import java.util.ArrayList;

@Data
@Builder
public class Performance {

    public enum STATUS {
        STARTED,
        COMPLETE
    }
    @Id
    private String id;
    private String testType;
    private ArrayList<String> progress = new ArrayList<>();
    private STATUS status;
}
