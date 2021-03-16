package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.pojos.Performance;
import in.springframework.learning.tutorial.pojos.Statistics;
import in.springframework.learning.tutorial.services.PerformanceService;
import in.springframework.learning.tutorial.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/statistics")
public class StatisticsEndpoint {
    @Autowired
    private StatisticsService statisticsService;

    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    @RequestMapping(value = "/{performanceRunId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE, "text/csv" } )
    public @ResponseBody List<Statistics> getStatistics(@PathVariable("performanceRunId") String performanceRunId) {

        return statisticsService.findAllByPerformanceRunId(performanceRunId);
    }
}
