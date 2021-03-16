package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.exceptions.EntityDoesnotExist;
import in.springframework.learning.tutorial.pojos.Department;
import in.springframework.learning.tutorial.pojos.Faculty;
import in.springframework.learning.tutorial.pojos.Specialization;
import in.springframework.learning.tutorial.repositories.DepartmentRepository;
import in.springframework.learning.tutorial.repositories.FacultyRepository;
import in.springframework.learning.tutorial.repositories.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/faculty")
public class FacultyEndpoint {
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private SpecializationRepository specializationRepository;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Faculty> getFaculties() {

        return facultyRepository.findAll();
    }

    @RequestMapping(value = "/search/{specializationCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Faculty> getFaculties(@PathVariable("specializationCode") String specializationCode) {

        Optional<Specialization> optionalSpecialization
                = specializationRepository.findByCode(specializationCode);
        if (optionalSpecialization.isPresent()) {
            List<Faculty> faculties = facultyRepository.findBySpecialization(optionalSpecialization.get().getId());
            return faculties;
        }
        throw new EntityDoesnotExist(String.format("Entity specialization doesn't exist for specialization code %s", specializationCode));
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Faculty> createFaculty(@RequestBody Faculty faculty) {

        return Optional.of(facultyRepository.save(faculty));
    }
}
