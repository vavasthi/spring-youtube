package in.springframework.learning.tutorial.services;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Aggregates;
import in.springframework.learning.tutorial.pojos.*;
import in.springframework.learning.tutorial.repositories.CourseEnrolledRepository;
import in.springframework.learning.tutorial.repositories.CourseOfferedRepository;
import in.springframework.learning.tutorial.repositories.PerformanceRepository;
import in.springframework.learning.tutorial.repositories.StatisticsRepository;
import in.springframework.learning.tutorial.utils.RandomUtilities;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;
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
    private final int cachedStudents = 10;

    private List<Student> randomStudents = new ArrayList<>();
    private List<CourseOffered> randomCourses;

    private SecureRandom random = new SecureRandom();

    public List<Performance> findAll() {
        return performanceRepository.findAll();
    }

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
        randomCourses = courseOfferedRepository.findAll();
    }
    public Performance save(Performance performance) {
        return performanceRepository.save(performance);
    }
    public Iterable<Student> getRandomStudent(int count) {

        return randomStudents;
    }
    public List<String> getRandomStudentId(int count) {

        return randomStudents.stream().map(s -> s.getId()).collect(Collectors.toList());
    }
    public void runTest(Performance performance)  {

        log.info("Running test.");
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
        queryStudentInName(10, performance);
        log.info(String.format("Query IN index @ base %d and count %d", base, count));
        queryStudentInCity(performance);
        log.info(String.format("Query by unindex @ base %d and count %d", base, count));
        queryStudentInCities(10, performance);
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
    public void createRandom(Long base,
                             Long count,
                             Performance performance) {

        List<Student> studentList = new ArrayList<>();
        studentService.deleteByBase(false);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (studentService.count() != base) {

            log.info("Base has changed. Clearing old data.");
            randomStudents.clear();
            studentService.deleteAll();
            addStudentRecords(base, cachedStudents, true);
        }
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        addStudentRecords(base + count - studentService.count(), cachedStudents, false);
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.INSERT, Statistics.RECORD_TYPE.STUDENT, count, performance);
        }
    }
    private void addStudentRecords(long count, int cacheSize, boolean base) {
        List<Student> studentList = new ArrayList<>();
        for (long i = 0; i < count; ++i) {
            if (studentList.size() >= 10000) {
                List<Student> savedStudents = studentService.saveAll(studentList);
                if (randomStudents.size() < cacheSize) {
                    while (savedStudents.size() > 0 && randomStudents.size() < cacheSize) {
                        randomStudents.add(savedStudents.remove(0));
                    }
                }
                studentList.clear();
            }
            studentList.add(Student.builder()
                    .name(RandomUtilities.createRandomName())
                    .city(RandomUtilities.createRandomCity())
                    .base(base)
                    .build());
        }
        if (studentList.size() > 0) {

            List<Student> savedStudents = studentService.saveAll(studentList);
            if (randomStudents.size() < 10) {
                while (savedStudents.size() > 0 && randomStudents.size() < 10) {
                    randomStudents.add(savedStudents.remove(0));
                }
            }
        }
    }
    public Optional<Student> queryById(Performance performance) {

        String id = getRandomStudentId(1).get(0);
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        Optional<Student> students = studentService.findById(id);
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY, Statistics.RECORD_TYPE.STUDENT, 1, performance);
        }
        return students;
    }
    public Iterable<Student> queryByIdIn(int count, Performance performance) {

        Iterable<String> ids = getRandomStudentId(count);
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        Iterable<Student> students = studentService.findByIdIn(ids);
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY_IN_ID, Statistics.RECORD_TYPE.STUDENT, count, performance);
        }
        return students;
    }
    public Iterable<Student> queryStudentInCity(Performance performance) {

        Iterable<Student> students = getRandomStudent(1);
        Student student = students.iterator().next();
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        students = studentService.findByCity(student.getCity());
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY_UNINDEXED, Statistics.RECORD_TYPE.STUDENT, 1, performance);
        }
        return students;
    }
    public Iterable<Student> queryStudentInCities(int count,
                                              Performance performance) {

        List<String> cities = new ArrayList<>();
        Iterable<Student> students = getRandomStudent(count);
        for (Student s : students) {
            cities.add(s.getCity());
        }
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        List<Student> studentList = new ArrayList<>();
        students = studentService.findByCityIn(cities);
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY_IN_UNINDEXED, Statistics.RECORD_TYPE.STUDENT, count, performance);
        }
        return students;
    }
    public List<Student> queryStudentByName(Performance performance) {

        Iterable<Student> students = getRandomStudent(1);
        Student student = students.iterator().next();
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        List<Student> studentList = studentService.findByName(student.getName());
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY_INDEXED, Statistics.RECORD_TYPE.STUDENT, 1, performance);
        }
        return studentList;
    }
    public Iterable<Student> queryStudentInName(int count,
                                            Performance performance) {

        Iterable<Student> students = getRandomStudent(count);
        List<String> names = new ArrayList<>();
        for (Student s : students) {
            names.add(s.getName());
        }
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        students = studentService.findByNameIn(names);
        if (performance != null) {

            saveStatistics(startTime, nanoStartTime, Statistics.OPERATION_TYPE.QUERY_IN_INDEXED, Statistics.RECORD_TYPE.STUDENT, count, performance);
        }
        return students;
    }
    @Transactional
    public List<CoursesEnrolled> randomEnrolOfferedCourse(int count, Performance performance) {

        List<CoursesEnrolled> coursesEnrolledArrayList = new ArrayList<>();
        Date startTime = new Date();
        long nanoStartTime = System.nanoTime();
        for (int i = 0; i < count; ++i) {

            Iterable<Student> chosenStudents = getRandomStudent(count);
            while(randomCourses.iterator().hasNext()) {

                CourseOffered chosenCourse = randomCourses.iterator().next();
                Student chosenStudent = chosenStudents.iterator().next();
                coursesEnrolledArrayList.add(CoursesEnrolled.builder()
                        .courseId(chosenCourse.getCourseId())
                        .courseOfferedId(chosenCourse.getId())
                        .status(CoursesEnrolled.Status.Enrolled)
                        .studentId(chosenStudent.getId())
                        .build());
            }
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
                .nanoseconds(System.nanoTime() - nanoStartTime)
                .build());
    }
}
