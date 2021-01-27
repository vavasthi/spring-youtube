package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.ds.entities.TimeSeriesEntity;
import in.springframework.learning.tutorial.ds.repositories.TimeSeriesRepository;
import in.springframework.learning.tutorial.exceptions.EntityDoesnotExist;
import in.springframework.learning.tutorial.pojos.*;
import in.springframework.learning.tutorial.repositories.CourseEnrolledRepository;
import in.springframework.learning.tutorial.repositories.CourseOfferedRepository;
import in.springframework.learning.tutorial.repositories.StatisticsRepository;
import in.springframework.learning.tutorial.repositories.StudentRepository;
import in.springframework.learning.tutorial.services.StudentService;
import in.springframework.learning.tutorial.utils.RandomUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student")
public class StudentEndpoint {
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseOfferedRepository courseOfferedRepository;
    @Autowired
    private CourseEnrolledRepository courseEnrolledRepository;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Student> getStudents() {

        return studentService.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Student> putTimeSeries(@RequestBody Student student) {

        return Optional.of(studentService.save(student));
    }
    @RequestMapping(value = "/enrol/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<CoursesEnrolled> enrolOfferedCourse(@PathVariable("id") String id,
                                                        @RequestBody CoursesEnrolled coursesEnrolled) {

        Optional<CourseOffered> optionalCourseOffered = courseOfferedRepository.findById(id);
        if (optionalCourseOffered.isPresent()) {
            CourseOffered courseOffered = optionalCourseOffered.get();
            coursesEnrolled.setCourseId(courseOffered.getCourseId());
            coursesEnrolled.setCourseOfferedId(courseOffered.getId());
            coursesEnrolled.setStatus(CoursesEnrolled.Status.Enrolled);
            return Optional.of(courseEnrolledRepository.save(coursesEnrolled));
        }
        throw new EntityDoesnotExist(String.format("Course offer %s doesn't exist.", id));
    }
}
