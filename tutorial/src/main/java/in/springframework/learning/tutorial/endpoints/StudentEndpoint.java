package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.ds.entities.TimeSeriesEntity;
import in.springframework.learning.tutorial.ds.repositories.TimeSeriesRepository;
import in.springframework.learning.tutorial.exceptions.EntityDoesnotExist;
import in.springframework.learning.tutorial.pojos.Course;
import in.springframework.learning.tutorial.pojos.CourseOffered;
import in.springframework.learning.tutorial.pojos.CoursesEnrolled;
import in.springframework.learning.tutorial.pojos.Student;
import in.springframework.learning.tutorial.repositories.CourseEnrolledRepository;
import in.springframework.learning.tutorial.repositories.CourseOfferedRepository;
import in.springframework.learning.tutorial.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentEndpoint {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseOfferedRepository courseOfferedRepository;
    @Autowired
    private CourseEnrolledRepository courseEnrolledRepository;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Student> getStudents() {

        return studentRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Student> putTimeSeries(@RequestBody Student student) {

        return Optional.of(studentRepository.save(student));
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
