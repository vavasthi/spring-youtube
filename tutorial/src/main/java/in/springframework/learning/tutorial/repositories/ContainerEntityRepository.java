package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.ContainerEntity;
import in.springframework.learning.tutorial.pojos.CoursesEnrolled;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContainerEntityRepository extends MongoRepository<ContainerEntity, String> {
}
