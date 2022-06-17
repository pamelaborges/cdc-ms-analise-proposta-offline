package br.com.via.infrastructure.siteproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.via.application.service.SitePropertiesService;
import reactor.core.publisher.Mono;

@Component
public class SitePropertiesSource {

	@Autowired
	private SitePropertiesService sitePropertiesService;

	public Mono<Boolean> isMicrosservicosKafkaHabilitado() {
		return this.sitePropertiesService.findUnique().flatMap(siteProperties -> Mono.just(siteProperties.isMicrosservicosKafkaHabilitado()));
	}

	public Mono<Boolean> isMsAnalisePropostaOfflineKafkaHabilitado() {
		return this.sitePropertiesService.findUnique().flatMap(siteProperties -> Mono.just(siteProperties.isMsAnalisePropostaOfflineKafkaHabilitado()));
	}
}
