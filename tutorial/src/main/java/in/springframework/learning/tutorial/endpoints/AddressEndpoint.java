package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.annotations.AuthenticatedUserOrAdminWithString;
import in.springframework.learning.tutorial.annotations.ListOutputAllForAdminOrCurrentUser;
import in.springframework.learning.tutorial.entities.UserEntity;
import in.springframework.learning.tutorial.exceptions.EntityDoesnotExist;
import in.springframework.learning.tutorial.exceptions.UserDoesnotExist;
import in.springframework.learning.tutorial.pojos.Address;
import in.springframework.learning.tutorial.repositories.AddressRepository;
import in.springframework.learning.tutorial.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/address")
public class AddressEndpoint {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;

    @RequestMapping(value = "/{usernameOrEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthenticatedUserOrAdminWithString
    public Optional<Address> getUserAddress(@PathVariable("usernameOrEmail") String usernameOrEmail) {

        Optional<UserEntity> optionalUserEntity
                = userRepository.findUserEntityByEmailOrUsername(usernameOrEmail, usernameOrEmail);
        if (optionalUserEntity.isPresent()) {
            Optional<Address> optionalAddress
                    = addressRepository.findByUserId(optionalUserEntity.get().getId());
            if (optionalAddress.isPresent()) {
                return optionalAddress;
            }
            throw new EntityDoesnotExist(String.format("Address for user %s does not exist", usernameOrEmail));
        }
        throw new EntityDoesnotExist(String.format("User %s does not exist", usernameOrEmail));
    }
    @RequestMapping(value = "/{usernameOrEmail}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @AuthenticatedUserOrAdminWithString
    public Optional<Address> createAddress(@PathVariable("usernameOrEmail") String usernameOrEmail,
                                           @RequestBody Address address) {

        Optional<UserEntity> optionalUserEntity
                = userRepository.findUserEntityByEmailOrUsername(usernameOrEmail, usernameOrEmail);
        if (optionalUserEntity.isPresent()) {
            address.setUserId(optionalUserEntity.get().getId());
            return Optional.of(addressRepository.save(address));
        }
        throw new EntityDoesnotExist(String.format("User %s does not exist", usernameOrEmail));
    }
}
