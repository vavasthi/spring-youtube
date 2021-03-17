package in.springframework.learning.tutorial.endpoints;

import in.springframework.learning.tutorial.caching.Contained1CacheService;
import in.springframework.learning.tutorial.caching.Contained2CacheService;
import in.springframework.learning.tutorial.caching.ContainerCacheService;
import in.springframework.learning.tutorial.exceptions.EntityDoesnotExist;
import in.springframework.learning.tutorial.pojos.*;
import in.springframework.learning.tutorial.repositories.CourseOfferedRepository;
import in.springframework.learning.tutorial.repositories.CourseRepository;
import in.springframework.learning.tutorial.repositories.SpecializationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@RestController
@RequestMapping("/container")
public class ContainerEndpoint {
    @Autowired
    private ContainerCacheService containerCacheService;
    @Autowired
    private Contained1CacheService contained1CacheService;
    @Autowired
    private Contained2CacheService contained2CacheService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<ContainerEntity> getCourses() {

        return containerCacheService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<ContainerEntity> getContainer(@PathVariable("id") String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return containerCacheService.findById(id);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<ContainerEntity> createContainer(@RequestBody ContainerEntity containerEntity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return containerCacheService.create(containerEntity);
    }
    @RequestMapping(value = "/{id}/contained1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Contained1Entity> createContainer(@RequestBody Contained1Entity contained1Entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return contained1CacheService.create(contained1Entity);
    }
    @RequestMapping(value = "/{id}/contained2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Contained2Entity> createContainer(@RequestBody Contained2Entity contained2Entity)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return contained2CacheService.create(contained2Entity);
    }

    @RequestMapping(value = "/contained1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Contained1Entity> getContained1()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return contained1CacheService.findAll();
    }
    @RequestMapping(value = "/contained2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<Contained2Entity> getContained2()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return contained2CacheService.findAll();
    }
    @RequestMapping(value = "/contained1/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Contained1Entity> deleteContained1(@PathVariable("id") String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return contained1CacheService.delete(id);
    }
    @RequestMapping(value = "/contained2/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Contained2Entity> deleteContained2(@PathVariable("id") String id)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return contained2CacheService.delete(id);
    }
}
