package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student, String> {
    List<Student> findByCityIn(List<String> cities);

    List<Student> findByCity(String city);

    List<Student> findByName(String name);

    List<Student> findByNameIn(List<String> names);

    Iterable<? extends Student> findByBase(boolean base);

    Long deleteStudentByBase(boolean base);
}
