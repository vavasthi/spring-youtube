package in.springframework.learning.tutorial.annotations;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('USER') and operationForCurrentUser(#id))")
public @interface AuthenticatedUserOrAdmin {
}
