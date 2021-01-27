package in.springframework.learning.tutorial.services;

import in.springframework.learning.tutorial.pojos.*;
import in.springframework.learning.tutorial.repositories.CourseEnrolledRepository;
import in.springframework.learning.tutorial.repositories.CourseOfferedRepository;
import in.springframework.learning.tutorial.repositories.PerformanceRepository;
import in.springframework.learning.tutorial.repositories.StatisticsRepository;
import in.springframework.learning.tutorial.utils.RandomUtilities;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PerformanceService {

    @Autowired
    private StatisticsRepository statisticsRepository;
    @Autowired
    private CourseOfferedRepository courseOfferedRepository;
    @Autowired
    private CourseEnrolledRepository courseEnrolledRepository;
    @Autowired
    private StudentService studentService;
    @Autowired
    private PerformanceRepository performanceRepository;

    private SecureRandom random = new SecureRandom();

    private CourseOffered[] courseOffered;
    private String[] studentIds;


    public enum TEST_TYPE {
        TEST1("runTest1"),
        TEST2("runTest2");

        public  final String method;
        TEST_TYPE(String method) {
            this.method = method;
        }
    }

    @PostConstruct
    private void init() {
        initialize();
    }
    public void initialize() {

        courseOffered = courseOfferedRepository.findAll().toArray(new CourseOffered[0]);
        studentIds = studentService.findAll().stream().map(Student::getId).collect(Collectors.toList()).toArray(new String[0]);
    }
    public Performance save(Performance performance) {
        return performanceRepository.save(performance);
    }
    public CourseOffered getRandomCourseOffered() {

        return courseOffered[random.nextInt(courseOffered.length)];
    }
    public Optional<Student> getRandomStudent() {

        return studentService.findById(studentIds[random.nextInt(studentIds.length)]);
    }
    public String getRandomStudentId() {

        return studentIds[random.nextInt(studentIds.length)];
    }
    public void runTest(Performance perf)  {

        Optional<Performance> optionalPerformance = performanceRepository.findById(perf.getId());
        log.info("Running test.");
        Performance performance = optionalPerformance.get();
        TEST_TYPE testType = TEST_TYPE.valueOf(performance.getTestType());
        Method m = null;
        try {
            m = this.getClass().getMethod(testType.method, Performance.class);
            m.invoke(this, performance);
            performance = performanceRepository.save(performance);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * This performance test, tests the insertion, query, deletion of student record with different base values
     * follwed by the same thing for courseEnrollments
     * @param performance
     */
    public void runTest1(Performance performance) {
        // Cleanup both the collections.

        studentService.deleteAll();
        courseEnrolledRepository.deleteAll();
        performanceRepository.deleteAll();
        insertStudentRecords(performance);
    }

    private void studentAllOperations(long base, long count, Performance performance) {

        createRandom(base, count, performance);
        log.info(String.format("Created student reords @ base %d and count %d", base, count));
        queryById(performance);
        log.info(String.format("Query by id @ base %d and count %d", base, count));
        queryByIdIn(10, performance);
        log.info(String.format("Query IN id @ base %d and count %d", base, count));
        queryStudentByName(performance);
        log.info(String.format("Query by index @ base %d and count %d", base, count));
        queryStudentInName(10L, performance);
        log.info(String.format("Query IN index @ base %d and count %d", base, count));
        queryStudentInCity(performance);
        log.info(String.format("Query by unindex @ base %d and count %d", base, count));
        queryStudentInCities(10L, performance);
        log.info(String.format("Query IN unindex @ base %d and count %d", base, count));
        performance.getProgress().add(String.format("Persistence and query done for base %d and count %d", base, count));
    }
    private void studentAllOperationsForAllCount(long base, Performance performance) {

        studentAllOperations(base,1, performance);
        studentAllOperations(base,10, performance);
        studentAllOperations(base,100, performance);
        studentAllOperations(base,1000, performance);
        studentAllOperations(base,10000, performance);
        studentAllOperations(base,100000, performance);
        studentAllOperations(base,1000000, performance);
    }
    private void insertStudentRecords(Performance performance) {
        // Database is empty. Base is 0
        studentAllOperationsForAllCount(0, performance);
        studentAllOperationsForAllCount(10, performance);
        studentAllOperationsForAllCount(100, performance);
        studentAllOperationsForAllCount(1000, performance);
        studentAllOperationsForAllCount(10000, performance);
        studentAllOperationsForAllCount(100000, performance);
        studentAllOperationsForAllCount(100000, performance);
        studentAllOperationsForAllCount(1000000, performance);

    }
    public void runTest2(Performance performance) {

        log.info("Running test 1" + performance.getId());
    }
    @Transactional
    public List<Student> createRandom(Long base,
                                      Long count,
                                      Performance performance) {
        studentService.adjustBase(base, studentIds);
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        List<Student> studentList = new ArrayList<>();
        for (long i = studentService.count(); i < base + count; ++i) {
            studentList.add(Student.builder()
                    .name(RandomUtilities.createRandomName())
                    .city(RandomUtilities.createRandomCity())
                    .build());
        }
        if (performance != null) {

            studentList =  studentService.saveAll(studentList);
            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.INSERT, Statistics.RECORD_TYPE.STUDENT, count, performance);
        }
        studentIds = studentService.findAll().stream().map(Student::getId).collect(Collectors.toList()).toArray(new String[0]);
        return studentList;
    }
    public Optional<Student> queryById(Performance performance) {

        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        Optional<Student> students
                = studentService.findById(getRandomStudentId());
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY, Statistics.RECORD_TYPE.STUDENT, 1, performance);
        }
        return students;
    }
    public Iterable<Student> queryByIdIn(int count, Performance performance) {

        List<String> ids = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            ids.add(getRandomStudentId());
        }
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        Iterable<Student> students
                = studentService.findByIdIn(ids);
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY_IN_ID, Statistics.RECORD_TYPE.STUDENT, count, performance);
        }
        return students;
    }
    public List<Student> queryStudentInCity(Performance performance) {

        List<String> cities = new ArrayList<>();
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        List<Student> studentList = new ArrayList<>();
        List<Student> students
                = studentService.findByCity(RandomUtilities.createRandomCity());
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY_UNINDEXED, Statistics.RECORD_TYPE.STUDENT, 1, performance);
        }
        return students;
    }
    public List<Student> queryStudentInCities(Long count,
                                              Performance performance) {

        List<String> cities = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            cities.add(RandomUtilities.createRandomCity());
        }
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        List<Student> studentList = new ArrayList<>();
        List<Student> students = studentService.findByCityIn(cities);
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY_IN_UNINDEXED, Statistics.RECORD_TYPE.STUDENT, count, performance);
        }
        return students;
    }
    public List<Student> queryStudentByName(Performance performance) {

        Optional<Student> optionalStudent = getRandomStudent();
        Student student = optionalStudent.get();
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        List<Student> studentList = studentService.findByName(student.getName());
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY_INDEXED, Statistics.RECORD_TYPE.STUDENT, 1, performance);
        }
        return studentList;
    }
    public List<Student> queryStudentInName(Long count,
                                            Performance performance) {

        List<String> names = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            names.add(getRandomStudent().get().getName());
        }
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        List<Student> studentList = new ArrayList<>();
        List<Student> students = studentService.findByNameIn(names);
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY_IN_INDEXED, Statistics.RECORD_TYPE.STUDENT, count, performance);
        }
        return students;
    }
    @Transactional
    public List<CoursesEnrolled> randomEnrolOfferedCourse(long count, Performance performance) {

        List<CoursesEnrolled> coursesEnrolledArrayList = new ArrayList<>();
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        for (int i = 0; i < count; ++i) {

            CourseOffered chosenCourse = getRandomCourseOffered();
            Student chosenStudent = getRandomStudent().get();
            coursesEnrolledArrayList.add(CoursesEnrolled.builder()
                    .courseId(chosenCourse.getCourseId())
                    .courseOfferedId(chosenCourse.getId())
                    .status(CoursesEnrolled.Status.Enrolled)
                    .studentId(chosenStudent.getId())
                    .build());
        }
        coursesEnrolledArrayList  =  courseEnrolledRepository.saveAll(coursesEnrolledArrayList);
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.INSERT, Statistics.RECORD_TYPE.ENROLMENT, count, performance);
        }
        return coursesEnrolledArrayList;
    }
    private void saveStatistics(Date startTime,
                                long nanoStartTime,
                                Statistics.OPERATION_TYPE operationType,
                                Statistics.RECORD_TYPE recordType,
                                long count,
                                Performance performance) {

        Date endTime = new Date();
        statisticsRepository.save(Statistics.builder()
                .endTime(endTime)
                .startTime(startTime)
                .count(count)
                .base(studentService.count())
                .recordType(recordType)
                .operationType(operationType)
                .performanceRunId(performance.getId())
                .milliseconds(System.nanoTime() - nanoStartTime)
                .build());
    }
}
