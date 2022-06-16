package br.com.via;

import javax.validation.Validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@EnableReactiveMongoAuditing(modifyOnCreate = false)
@SpringBootApplication(exclude = { MongoReactiveAutoConfiguration.class, MongoAutoConfiguration.class })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

//	@Bean
//	public Validator validator() {
//
//		return new LocalValidatorFactoryBean();
//	}
}
