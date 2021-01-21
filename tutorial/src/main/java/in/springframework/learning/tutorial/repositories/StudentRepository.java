package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StudentRepository extends MongoRepository<Student, String> {
}
