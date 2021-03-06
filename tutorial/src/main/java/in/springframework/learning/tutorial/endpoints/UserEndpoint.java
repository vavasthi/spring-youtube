package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.annotations.AuthenticatedUserOrAdmin;
import in.springframework.learning.tutorial.annotations.ListOutputAllForAdminOrCurrentUser;
import in.springframework.learning.tutorial.annotations.NewUserCreationAuthentication;
import in.springframework.learning.tutorial.entities.UserEntity;
import in.springframework.learning.tutorial.exceptions.UserAlreadyExists;
import in.springframework.learning.tutorial.exceptions.UserCreationUsernameMismatchException;
import in.springframework.learning.tutorial.exceptions.UserDoesnotExist;
import in.springframework.learning.tutorial.repositories.UserRepository;
import in.springframework.learning.tutorial.security.UsernamePasswordPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
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
    public Iterable<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthenticatedUserOrAdmin
    public Optional<UserEntity> getUser(@PathVariable("id") Long id) {
        Optional<UserEntity> oue = userRepository.findById(id);
        if (oue.isPresent()) {
            return oue;
        }
        throw new UserDoesnotExist(id);
    }
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @NewUserCreationAuthentication
    public Optional<UserEntity> createUser(@RequestBody UserEntity userEntity,
                                           Authentication authentication) {
        UsernamePasswordPrincipal principal
                = UsernamePasswordPrincipal.class.cast(authentication.getPrincipal());
        String username = principal.getUsername().get();
        if (!username.equals(userEntity.getUsername())) {
            throw new UserCreationUsernameMismatchException(username, userEntity.getUsername());
        }
        Optional<UserEntity> oue
                = userRepository.findUserEntityByEmailOrUsername(userEntity.getEmail(), userEntity.getUsername());
        if (oue.isPresent()) {
            throw new UserAlreadyExists(userEntity.getEmail(), userEntity.getUsername());
        }
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return Optional.of(userRepository.save(userEntity));
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthenticatedUserOrAdmin
    public Optional<UserEntity> deleteUser(@PathVariable("id") Long id) {
        Optional<UserEntity> oue = userRepository.findById(id);
        if (oue.isPresent()) {

            userRepository.deleteById(id);
            return oue;
        }
        throw new UserDoesnotExist(id);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthenticatedUserOrAdmin
    public Optional<UserEntity> deleteUser(@PathVariable("id") Long id,
                                           @RequestBody UserEntity updatedObject) {
        Optional<UserEntity> oue = userRepository.findById(id);
        if (oue.isPresent()) {

            UserEntity storedObject = oue.get();
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
