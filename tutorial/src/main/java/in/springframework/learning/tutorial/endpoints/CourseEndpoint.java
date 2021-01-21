package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.exceptions.EntityDoesnotExist;
import in.springframework.learning.tutorial.pojos.Course;
import in.springframework.learning.tutorial.pojos.CourseOffered;
import in.springframework.learning.tutorial.pojos.Faculty;
import in.springframework.learning.tutorial.pojos.Specialization;
import in.springframework.learning.tutorial.repositories.CourseOfferedRepository;
import in.springframework.learning.tutorial.repositories.CourseRepository;
import in.springframework.learning.tutorial.repositories.FacultyRepository;
import in.springframework.learning.tutorial.repositories.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/course")
public class CourseEndpoint {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseOfferedRepository courseOfferedRepository;
    @Autowired
    private SpecializationRepository specializationRepository;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Course> getCourses() {

        return courseRepository.findAll();
    }

    @RequestMapping(value = "/search/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Course> getCourses(@PathVariable("code") String code) {

        return courseRepository.findByCode(code);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Course> createCourse(@RequestBody Course course) {

        return Optional.of(courseRepository.save(course));
    }

    @RequestMapping(value = "/offer/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<CourseOffered> offerCourse(@PathVariable("id") String id,
                                               @RequestBody CourseOffered courseOffered) {

        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            courseOffered.setCourseId(id);
            return Optional.of(courseOfferedRepository.save(courseOffered));
        }
        throw new EntityDoesnotExist(String.format("Course %s doesn't exist.", id));
    }
}
