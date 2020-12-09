package in.springframework.learning.tutorial.ds.repositories;

import in.springframework.learning.tutorial.ds.entities.TimeSeriesEntity;
import org.springframework.data.repository.CrudRepository;

public interface TimeSeriesRepository extends CrudRepository<TimeSeriesEntity, Long> {
}
