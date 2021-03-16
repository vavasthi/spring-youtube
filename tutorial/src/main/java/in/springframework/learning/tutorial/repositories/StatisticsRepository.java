package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Course;
import in.springframework.learning.tutorial.pojos.Statistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface StatisticsRepository extends MongoRepository<Statistics, String> {

    @Query("{'performanceRunId' : ?0}")
    List<Statistics> findAllByPerformanceRunId(String performanceRunId);
}
