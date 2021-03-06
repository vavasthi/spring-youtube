package in.springframework.learning.tutorial.security;

import com.google.gson.Gson;
import in.springframework.learning.tutorial.exceptions.BaseException;
import in.springframework.learning.tutorial.exceptions.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
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
    private final String TOKEN_HEADER = "X-token";
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
        try {

            if (httpRequest.getRequestURI().toString().equals("/authenticate") ||
                    (httpRequest.getRequestURI().toString().equals("/user") &&
                            httpRequest.getMethod().equals("POST"))) {
                Optional<String> username = Optional.of(httpRequest.getHeader(USERNAME_HEADER));
                Optional<String> password = Optional.ofNullable(httpRequest.getHeader(PASSWORD_HEADER));
                UsernamePasswordPrincipal principal = new UsernamePasswordPrincipal(username, password);
                /**
                 * If the URI is authenticate then we expect both the username and password, if the
                 * URI is /user and method is POST, then we only expect username and it is a
                 * new user creation request.
                 */
                if (httpRequest.getRequestURI().toString().equals("/user") &&
                        httpRequest.getMethod().equals("POST")) {

                    principal = new UsernamePasswordPrincipal(username);

                }
                UsernamePasswordAuthenticationToken requestToken
                        = new UsernamePasswordAuthenticationToken(principal, password);
                Authentication responseAuthentication = authenticationManager.authenticate(requestToken);
                if (responseAuthentication != null && responseAuthentication.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(responseAuthentication);
                }
                else {

                    throw new BadCredentialsException("Unable to authenticate user.");
                }
            }
            else {

                TokenPrincipal.TYPE_OF_TOKEN typeOfToken = TokenPrincipal.TYPE_OF_TOKEN.AUTH_TOKEN;
                if (httpRequest.getRequestURI().toString().equals("/authenticate/refresh")) {
                    typeOfToken = TokenPrincipal.TYPE_OF_TOKEN.REFRESH_TOKEN;
                }
                Optional<String> token = Optional.ofNullable(httpRequest.getHeader(TOKEN_HEADER));
                if (token.isPresent()) {

                    TokenPrincipal principal = new TokenPrincipal(typeOfToken, token);
                    PreAuthenticatedAuthenticationToken requestToken
                            = new PreAuthenticatedAuthenticationToken(principal, null);
                    Authentication responseAuthentication = authenticationManager.authenticate(requestToken);
                    if (responseAuthentication != null && responseAuthentication.isAuthenticated()) {
                        SecurityContextHolder.getContext().setAuthentication(responseAuthentication);
                    }
                    else {

                        throw new BadCredentialsException("Unable to authenticate user.");
                    }
                }
                else {
                    throw new BadCredentialsException("No token was present in the request. Authentication Failed.");
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }
        catch(Exception ex) {
            handleException(ex, httpResponse);
        }
    }
    private void handleException(Exception ex, HttpServletResponse response) {

        try {

            if (ex instanceof BaseException) {
                BaseException bex = BaseException.class.cast(ex);
                int errorCode = bex.getErrorCode();
                String message = bex.getMessage();
                handleExceptionMsg(errorCode, message, response);
            }
            else if (ex instanceof BadCredentialsException) {
                BadCredentialsException bce = BadCredentialsException.class.cast(ex);
                handleExceptionMsg(HttpStatus.UNAUTHORIZED.value(), bce.getMessage(), response);
            }
            else {
                ex.printStackTrace();
                throw new InternalAuthenticationServiceException("Unknown error");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalAuthenticationServiceException("Unknown error");
        }
    }
    private void handleExceptionMsg(int errorCode, String message, HttpServletResponse response) throws IOException {
        ExceptionResponse er = new ExceptionResponse(errorCode, message);
        response.setStatus(errorCode);
        Gson gson = new Gson();
        String responseBody = gson.toJson(er);
        response.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().print(responseBody);
    }
}
