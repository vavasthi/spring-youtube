package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.pojos.Department;
import in.springframework.learning.tutorial.pojos.Student;
import in.springframework.learning.tutorial.repositories.DepartmentRepository;
import in.springframework.learning.tutorial.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/department")
public class DepartmentEndpoint {
    @Autowired
    private DepartmentRepository departmentRepository;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Department> getDepartments() {

        return departmentRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Department> putTimeSeries(@RequestBody Department department) {

        return Optional.of(departmentRepository.save(department));
    }
}
