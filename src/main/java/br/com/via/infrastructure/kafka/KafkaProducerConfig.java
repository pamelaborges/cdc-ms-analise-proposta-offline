package br.com.via.infrastructure.kafka;

import java.util.Map;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaProducerConfig {

	@Bean
	public ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate(KafkaProperties properties) {
		final Map<String, Object> props = properties.buildProducerProperties();
		return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
	}

}
