package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Department;
import in.springframework.learning.tutorial.pojos.Faculty;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepository extends MongoRepository<Department, String> {
}
