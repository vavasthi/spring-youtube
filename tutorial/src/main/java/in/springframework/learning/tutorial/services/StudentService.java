package in.springframework.learning.tutorial.services;

import in.springframework.learning.tutorial.pojos.*;
import in.springframework.learning.tutorial.repositories.CourseEnrolledRepository;
import in.springframework.learning.tutorial.repositories.StatisticsRepository;
import in.springframework.learning.tutorial.repositories.StudentRepository;
import in.springframework.learning.tutorial.utils.RandomUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StatisticsRepository statisticsRepository;
    @Autowired
    private PerformanceService performanceService;
    @Autowired
    private CourseEnrolledRepository courseEnrolledRepository;


    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public Optional<Student> findById(String id) {
        return studentRepository.findById(id);
    }
    public void deleteAll() {
        studentRepository.deleteAll();
    }

    public List<Student> saveAll(List<Student> studentList) {
        return studentRepository.saveAll(studentList);
    }

    public long count() {
        return studentRepository.count();
    }
    public List<Student> findByCityIn(List<String> cities) {
        return studentRepository.findByCityIn(cities);
    }

    public List<Student> findByCity(String city) {
        return studentRepository.findByCity(city);
    }

    public Iterable<Student> findByIdIn(Iterable<String> id) {
        return studentRepository.findAllById(id);
    }

    public List<Student> findByName(String name) {
        return studentRepository.findByName(name);
    }

    public List<Student> findByNameIn(List<String> names) {
        return studentRepository.findByNameIn(names);
    }

    public void adjustBase(Long base, String[] studentIds) {

        long existing = studentRepository.count();
        if (existing > base) {
            for (int i = 0; i < existing - base; ++i) {
                studentRepository.deleteById(studentIds[i]);
            }
        }
    }
}
