package in.springframework.learning.tutorial.customexpressions;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public class TutorialCustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }
        final String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();
        return hasPrivilege(authentication, targetType, permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId,
                                 String targetType,
                                 Object permission) {
        if (authentication == null || targetType == null || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(authentication, targetType, permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Authentication authentication, String targetType, String permission) {
        for (final GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.getAuthority().startsWith(targetType) &&
                    grantedAuthority.getAuthority().contains(permission)) {
                return true;
            }
        }
        return false;
    }
}
