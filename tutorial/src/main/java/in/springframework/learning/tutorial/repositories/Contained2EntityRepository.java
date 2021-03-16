package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Contained1Entity;
import in.springframework.learning.tutorial.pojos.Contained2Entity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Contained2EntityRepository extends MongoRepository<Contained2Entity, String> {
}
