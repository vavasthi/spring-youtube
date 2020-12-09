package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.ds.repositories.TimeSeriesRepository;
import in.springframework.learning.tutorial.ds.entities.TimeSeriesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/timeseries")
public class TimeSeriesEndpoint {

    @Autowired
    private TimeSeriesRepository timeSeriesRepository;
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<TimeSeriesEntity> putTimeSeries(@RequestBody TimeSeriesEntity tse) {

        tse.setTimestamp(new Date());
        return Optional.of(timeSeriesRepository.save(tse));
    }
}
