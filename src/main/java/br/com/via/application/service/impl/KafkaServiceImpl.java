package br.com.via.application.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.via.application.service.KafkaService;
import br.com.viavarejo.cdc.common.exceptions.BusinessException;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Service
public class KafkaServiceImpl<T> implements KafkaService<T> {

	@Autowired
	private ReactiveKafkaProducerTemplate<String, String> kafkaProducerTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public Mono<SenderResult<Void>> send(final String topic, final T model) {
		try {
			final String json = objectMapper.writeValueAsString(model);
			return kafkaProducerTemplate.send(topic, json);
		} catch (JsonProcessingException e) {
			return Mono.error(new BusinessException("Ocorreu um erro ao converter o Modelo para JSON."));
		}
	}
}
