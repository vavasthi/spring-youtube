package in.springframework.learning.tutorial;

import in.springframework.learning.tutorial.ds.repositories.TimeSeriesRepository;
import in.springframework.learning.tutorial.ds.entities.TimeSeriesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Date;

@SpringBootApplication(scanBasePackages = {"in.springframework.learning.tutorial"}, exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class TutorialApplication {

	@Autowired
	private TimeSeriesRepository timeSeriesRepository;
	public static void main(String[] args) {
		SpringApplication.run(TutorialApplication.class, args);
	}

	@PostConstruct
	@Transactional
	public void initialize() {
		TimeSeriesEntity tse = new TimeSeriesEntity();
		tse.setKey("KEY1");
		tse.setTimestamp(new Date());
		tse.setValue("V1");
		tse = timeSeriesRepository.save(tse);
	}
	@Bean(name = "passwordEncoder")
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

}
