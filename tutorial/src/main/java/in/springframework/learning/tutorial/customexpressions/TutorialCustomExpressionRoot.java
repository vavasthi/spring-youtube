package in.springframework.learning.tutorial.customexpressions;

import in.springframework.learning.tutorial.pojos.User;
import in.springframework.learning.tutorial.repositories.UserRepository;
import in.springframework.learning.tutorial.security.TokenPrincipal;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public class TutorialCustomExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;

    private final UserRepository userRepository;
    public TutorialCustomExpressionRoot(Authentication authentication, UserRepository userRepository) {
        super(authentication);
        this.userRepository = userRepository;
    }
    public boolean  operationForCurrentUser(Long id) {
        Optional<String> optionalUsername = TokenPrincipal.class.cast(this.getPrincipal()).getUsername();
        if (optionalUsername.isPresent()) {
            String username = optionalUsername.get();
            Optional<User> oue = userRepository.findUserByUsername(username);
            if (oue.isPresent()) {
                return oue.get().getId().equals(id);
            }
        }
        return false;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {

        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }
}
