package in.springframework.learning.tutorial.repositories;

import in.springframework.learning.tutorial.pojos.Contained1Entity;
import in.springframework.learning.tutorial.pojos.ContainerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface Contained1EntityRepository extends MongoRepository<Contained1Entity, String> {
}
