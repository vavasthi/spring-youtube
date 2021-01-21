package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface AddressRepository extends MongoRepository<Address, String> {

    @Query("{'userId':?0}")
    Optional<Address> findByUserId(Long userId);
}
