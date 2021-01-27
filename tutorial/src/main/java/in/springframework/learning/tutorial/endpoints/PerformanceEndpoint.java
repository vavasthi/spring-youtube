package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.exceptions.EntityDoesnotExist;
import in.springframework.learning.tutorial.pojos.CourseOffered;
import in.springframework.learning.tutorial.pojos.CoursesEnrolled;
import in.springframework.learning.tutorial.pojos.Performance;
import in.springframework.learning.tutorial.pojos.Student;
import in.springframework.learning.tutorial.repositories.CourseEnrolledRepository;
import in.springframework.learning.tutorial.repositories.CourseOfferedRepository;
import in.springframework.learning.tutorial.services.PerformanceService;
import in.springframework.learning.tutorial.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/performance")
public class PerformanceEndpoint {
    @Autowired
    private PerformanceService performanceService;

    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    @RequestMapping(value = "/test1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Performance> runTest1() {

        final Performance perf = Performance.builder()
                .testType(PerformanceService.TEST_TYPE.TEST1.name())
                .status(Performance.STATUS.STARTED)
                .progress(new ArrayList<>())
                .build();
        perf.getProgress().add(String.format("Started performace test at %s", new Date()));
        final Performance performance = performanceService.save(perf);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                performanceService.runTest(performance);
                performance.getProgress().add("Completed");
                performance.setStatus(Performance.STATUS.COMPLETE);
                performanceService.save(performance);

            }
        });
        return Optional.of(performance);
    }
}
