package br.com.via.application.service;

import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

public interface KafkaService<T> {

	Mono<SenderResult<Void>> send(String topic, T model);

}
