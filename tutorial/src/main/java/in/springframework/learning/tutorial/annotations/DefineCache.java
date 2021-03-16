package in.springframework.learning.tutorial.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //can use in class only.
public @interface DefineCache {
    String name();
    String prefix();
    long expiry();
}