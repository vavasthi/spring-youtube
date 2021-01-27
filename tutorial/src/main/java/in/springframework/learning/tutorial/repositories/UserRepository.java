package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, Long> {

    @Query("{'$or':[{'email' : ?0}, {'username': ?1} ] }")
    Optional<User> findUserEntityByEmailOrUsername(@Param("email") String email,
                                                   @Param("username") String username);

    @Query("{'username' : ?0}")
    Optional<User> findUserByUsername(@Param("username") String username);

    @Query("{'authToken' : ?0}")
    Optional<User> findUserByAuthToken(@Param("authToken") String authToken);

    @Query("{'refreshToken' : ?0}")
    Optional<User> findUserByRefreshToken(@Param("refreshToken") String refreshToken);
}
