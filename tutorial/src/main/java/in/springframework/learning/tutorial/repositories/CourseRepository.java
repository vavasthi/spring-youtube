package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Course;
import in.springframework.learning.tutorial.pojos.Faculty;
import in.springframework.learning.tutorial.pojos.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String> {

    @Query("{'code' : ?0}")
    List<Course> findByCode(String code);
}
