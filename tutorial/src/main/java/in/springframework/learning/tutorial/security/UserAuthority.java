package in.springframework.learning.tutorial.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserAuthority implements GrantedAuthority {

    public UserAuthority(String authority) {
        this.authority  = authority;
    }
    @Override
    public String getAuthority() {
        return authority;
    }
    public static Set<UserAuthority> getAuthoritiesFromRoles(List<Role> roles) {
        return roles.stream()
                .map(r -> new UserAuthority(r.name()))
                .collect(Collectors.toSet());
    }
    public static Set<UserAuthority> getAuthoritiesFromRoles(long mask) {
        return Role
                .createFromMask(mask)
                .stream()
                .map(r -> new UserAuthority(r.name()))
                .collect(Collectors.toSet());
    }
    private final String authority;
}
