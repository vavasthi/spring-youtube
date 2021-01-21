package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Faculty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FacultyRepository extends MongoRepository<Faculty, String> {

    @Query("{'specializations' : ?0}")
    List<Faculty> findBySpecialization(String specializationCode);
}
