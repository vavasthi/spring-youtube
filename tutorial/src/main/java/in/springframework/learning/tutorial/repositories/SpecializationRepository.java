package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Faculty;
import in.springframework.learning.tutorial.pojos.Specialization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface SpecializationRepository extends MongoRepository<Specialization, String> {
    @Query("{'code' : ?0}")
    Optional<Specialization> findByCode(String code);
}
