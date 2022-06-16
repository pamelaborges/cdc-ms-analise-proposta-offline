package br.com.via.domain.model.primary;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import br.com.viavarejo.cp.core.domain.model.entity.SiteProperties;
import br.com.viavarejo.cp.model.adb.ModoOperacao;
import reactor.core.publisher.Mono;

@Repository
public interface SitePropertiesRepository extends ReactiveMongoRepository<SiteProperties, String> {

	Mono<SiteProperties> findByModoOperacao(ModoOperacao modoOperacao);

}
