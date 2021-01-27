package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.annotations.AuthenticatedUserOrAdmin;
import in.springframework.learning.tutorial.annotations.ListOutputAllForAdminOrCurrentUser;
import in.springframework.learning.tutorial.annotations.NewUserCreationAuthentication;
import in.springframework.learning.tutorial.pojos.User;
import in.springframework.learning.tutorial.exceptions.UserAlreadyExists;
import in.springframework.learning.tutorial.exceptions.UserCreationUsernameMismatchException;
import in.springframework.learning.tutorial.exceptions.UserDoesnotExist;
import in.springframework.learning.tutorial.repositories.UserRepository;
import in.springframework.learning.tutorial.security.UsernamePasswordPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserEndpoint {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ListOutputAllForAdminOrCurrentUser
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthenticatedUserOrAdmin
    public Optional<User> getUser(@PathVariable("id") Long id) {
        Optional<User> oue = userRepository.findById(id);
        if (oue.isPresent()) {
            return oue;
        }
        throw new UserDoesnotExist(id);
    }
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @NewUserCreationAuthentication
    public Optional<User> createUser(@RequestBody User user,
                                     Authentication authentication) {
        UsernamePasswordPrincipal principal
                = UsernamePasswordPrincipal.class.cast(authentication.getPrincipal());
        String username = principal.getUsername().get();
        if (!username.equals(user.getUsername())) {
            throw new UserCreationUsernameMismatchException(username, user.getUsername());
        }
        Optional<User> oue
                = userRepository.findUserEntityByEmailOrUsername(user.getEmail(), user.getUsername());
        if (oue.isPresent()) {
            throw new UserAlreadyExists(user.getEmail(), user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthenticatedUserOrAdmin
    public Optional<User> deleteUser(@PathVariable("id") Long id) {
        Optional<User> oue = userRepository.findById(id);
        if (oue.isPresent()) {

            userRepository.deleteById(id);
            return oue;
        }
        throw new UserDoesnotExist(id);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthenticatedUserOrAdmin
    public Optional<User> deleteUser(@PathVariable("id") Long id,
                                     @RequestBody User updatedObject) {
        Optional<User> oue = userRepository.findById(id);
        if (oue.isPresent()) {

            User storedObject = oue.get();
            /**
             * The object is found and we need to update it.
             */
            if (updatedObject.getPassword()!= null &&
                    !passwordEncoder.matches(updatedObject.getPassword(), storedObject.getPassword())) {
                storedObject.setPassword(passwordEncoder.encode(updatedObject.getPassword()));
            }
            if (updatedObject.getEmail() != null &&
                    !updatedObject.getEmail().equals(storedObject.getEmail())) {
                storedObject.setEmail(updatedObject.getEmail());
            }
            userRepository.save(storedObject);
            return userRepository.findById(id);

        }
        throw new UserDoesnotExist(id);
    }
}
