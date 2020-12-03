package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    @Query("SELECT ue from UserEntity ue where ue.email = :email or ue.username = :username")
    Optional<UserEntity> findUserEntityByEmailOrUsername(@Param("email") String email,
                                                         @Param("username") String username);
}
