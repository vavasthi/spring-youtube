package in.springframework.learning.tutorial.annotations;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
@PostFilter("hasAuthority('ADMIN') or filterObject.username == authentication.name")
public @interface ListOutputAllForAdminOrCurrentUser {
}
