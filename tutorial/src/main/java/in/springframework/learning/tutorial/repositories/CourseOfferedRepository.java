package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Course;
import in.springframework.learning.tutorial.pojos.CourseOffered;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseOfferedRepository extends MongoRepository<CourseOffered, String> {
}
