package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.exceptions.EntityDoesnotExist;
import in.springframework.learning.tutorial.pojos.Department;
import in.springframework.learning.tutorial.pojos.Specialization;
import in.springframework.learning.tutorial.repositories.DepartmentRepository;
import in.springframework.learning.tutorial.repositories.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/specialization")
public class SpecializationEndpoint {
    @Autowired
    private SpecializationRepository specializationRepository;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Specialization> getSpecializations() {

        return specializationRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Specialization> putTimeSeries(@RequestBody Specialization specialization) {

        return Optional.of(specializationRepository.save(specialization));
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Specialization> putTimeSeries(@PathVariable("id") String id) {

        Optional<Specialization> optionalSpecialization = specializationRepository.findById(id);
        if (optionalSpecialization.isPresent()) {
            specializationRepository.delete(optionalSpecialization.get());
            return optionalSpecialization;
        }
        throw new EntityDoesnotExist(String.format("Specialization %s doesn't exist.", id));
    }
}
