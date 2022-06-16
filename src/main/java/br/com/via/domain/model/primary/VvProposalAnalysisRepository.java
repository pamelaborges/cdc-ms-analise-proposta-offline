package br.com.via.domain.model.primary;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import br.com.viavarejo.cp.core.domain.model.entity.VvProposalAnalysis;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VvProposalAnalysisRepository extends ReactiveCrudRepository<VvProposalAnalysis, String> {

	@Query(value = "{ 'cdEmpgcbCttPpo': ?0, 'cdFilCttPpo': ?1, 'cdCttppo': ?2, 'cdCttppoDvr': ?3, 'cdCclogIdf': ?4 }", count = true)
	Mono<Long> countPropostaPor(Integer cdEmpgcbCttPpo, Integer cdFilCttPpo, Integer cdCttppo, Integer cdCttppoDvr,
			Integer cdCclogIdf);

	@Query(value = "{ 'cdCttppo': ?0, 'cdCttppoDvr': ?1, 'cdEmpgcbCttPpo': ?2, 'cdFilCttPpo': ?3 }", fields = "{ uuid: 1 }")
	Flux<VvProposalAnalysis> obterUiid(Integer cdCttppo, Integer cdCttppoDvr, Integer cdEmpgcbCttPpo,
			Integer cdFilCttPpo);
}

