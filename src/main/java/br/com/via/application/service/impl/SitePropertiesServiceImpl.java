package br.com.via.application.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.via.application.service.SitePropertiesService;
import br.com.via.domain.model.primary.SitePropertiesRepository;
import br.com.viavarejo.cp.core.domain.model.entity.SiteProperties;
import br.com.viavarejo.cp.model.adb.ModoOperacao;
import reactor.core.publisher.Mono;

@Service
public class SitePropertiesServiceImpl implements SitePropertiesService {

	@Autowired
	private SitePropertiesRepository sitePropertiesRepository;

	@Override
	@Cacheable("siteProperties")
	public Mono<SiteProperties> findUnique() {
		return this.sitePropertiesRepository.findByModoOperacao(ModoOperacao.OFFLINE).cache();
	}

}
