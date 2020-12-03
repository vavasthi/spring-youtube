package in.springframework.learning.tutorial.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AuthenticationFilter extends GenericFilterBean {

    private final String USERNAME_HEADER = "X-username";
    private final String PASSWORD_HEADER = "X-password";
    private final AuthenticationManager authenticationManager;
    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = HttpServletRequest.class.cast(servletRequest);
        HttpServletResponse httpResponse = HttpServletResponse.class.cast(servletResponse);
        if (httpRequest.getRequestURI().toString().equals("/authenticate")) {
            Optional<String> username = Optional.of(httpRequest.getHeader(USERNAME_HEADER));
            Optional<String> password = Optional.of(httpRequest.getHeader(PASSWORD_HEADER));
            UsernamePasswordPrincipal principal = new UsernamePasswordPrincipal(username, password);
            UsernamePasswordAuthenticationToken requestToken
                    = new UsernamePasswordAuthenticationToken(principal, password);
            Authentication responseAuthentication = authenticationManager.authenticate(requestToken);
            if (responseAuthentication != null && responseAuthentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(responseAuthentication);
            }
            else {

                throw new InternalAuthenticationServiceException("Unable to authenticate user.");
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
