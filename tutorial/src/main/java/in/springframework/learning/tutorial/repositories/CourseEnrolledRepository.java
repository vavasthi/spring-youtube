package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.CourseOffered;
import in.springframework.learning.tutorial.pojos.CoursesEnrolled;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseEnrolledRepository extends MongoRepository<CoursesEnrolled, String> {
}
