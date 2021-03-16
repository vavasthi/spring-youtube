package in.springframework.learning.tutorial.caching;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyCacheTripleValue  implements Serializable {
    private Object key1;
    private Object key2;
    private Object key3;
}
