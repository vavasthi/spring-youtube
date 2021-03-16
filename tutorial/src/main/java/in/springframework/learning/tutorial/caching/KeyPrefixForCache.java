package in.springframework.learning.tutorial.caching;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyPrefixForCache {

    private String prefix;
    private Object key;
}
