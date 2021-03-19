package in.springframework.learning.tutorial.pojos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KeyForContainers<I> implements Serializable {

    I id;
    I containedId;
    String prefix;
}
