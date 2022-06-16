package br.com.via.infrastructure.mongo.primary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import br.com.via.infrastructure.mongo.MongoQualifiers;

/**
 * Primary MongoDB reactive configuration, which provides a specific database for the "br.com.via.domain.primary" package repositories.
 * 
 * Main reference: https://visweshwar.medium.com/spring-reactive-multi-mongotemplate-configuration-593e949456be
 * 
 * @author falvojr
 */
@Configuration
@EnableReactiveMongoRepositories(
		basePackages = "br.com.via.domain.model.primary", 
		reactiveMongoTemplateRef = MongoQualifiers.MONGO_PRIMARY
)
public class PrimaryReactiveMongoConfig {

	@Autowired
	private PrimaryMongoProperties mongoProperties;

	@Primary
	@Bean
	public MongoClient reactiveMongoClientPrimary() {
		return MongoClients.create(new ConnectionString(mongoProperties.getUri()));
	}

	@Primary
	@Bean(MongoQualifiers.MONGO_PRIMARY)
	public ReactiveMongoTemplate reactiveMongoTemplate() {
		return new ReactiveMongoTemplate(reactiveMongoClientPrimary(), mongoProperties.getDatabase());
	}
}