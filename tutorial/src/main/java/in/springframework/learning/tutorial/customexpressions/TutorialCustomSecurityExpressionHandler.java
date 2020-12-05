package in.springframework.learning.tutorial.customexpressions;

import in.springframework.learning.tutorial.repositories.UserRepository;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class TutorialCustomSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private AuthenticationTrustResolver trustResolver
            = new AuthenticationTrustResolverImpl();

    private final UserRepository userRepository;
    public TutorialCustomSecurityExpressionHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {

        TutorialCustomExpressionRoot root = new TutorialCustomExpressionRoot(authentication, userRepository);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}
