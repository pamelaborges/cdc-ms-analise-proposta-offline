package br.com.via.application.service;

import br.com.viavarejo.cp.core.domain.model.entity.SiteProperties;
import reactor.core.publisher.Mono;

public interface SitePropertiesService {

	Mono<SiteProperties> findUnique();

}
