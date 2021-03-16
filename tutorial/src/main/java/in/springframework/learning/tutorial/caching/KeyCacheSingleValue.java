package in.springframework.learning.tutorial.caching;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyCacheSingleValue  implements Serializable {
    private Object key1;
}
