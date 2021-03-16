package in.springframework.learning.tutorial.services;

import in.springframework.learning.tutorial.pojos.Statistics;
import in.springframework.learning.tutorial.repositories.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;
    public List<Statistics> findAllByPerformanceRunId(String performanceRunId) {
        return statisticsRepository.findAllByPerformanceRunId(performanceRunId);
    }
}
