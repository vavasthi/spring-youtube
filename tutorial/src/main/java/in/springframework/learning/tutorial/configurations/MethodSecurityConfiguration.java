package in.springframework.learning.tutorial.configurations;

import in.springframework.learning.tutorial.customexpressions.TutorialCustomPermissionEvaluator;
import in.springframework.learning.tutorial.customexpressions.TutorialCustomSecurityExpressionHandler;
import in.springframework.learning.tutorial.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Autowired
    private UserRepository userRepository;
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        TutorialCustomSecurityExpressionHandler expressionHandler = new TutorialCustomSecurityExpressionHandler(userRepository);
        expressionHandler.setPermissionEvaluator(new TutorialCustomPermissionEvaluator());
        return expressionHandler;
    }
}
