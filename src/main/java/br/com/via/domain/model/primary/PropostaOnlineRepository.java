package br.com.via.domain.model.primary;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import br.com.viavarejo.cp.core.domain.model.entity.PropostaOnline;
import reactor.core.publisher.Mono;

@Repository
public interface PropostaOnlineRepository extends ReactiveCrudRepository<PropostaOnline, String> {

	Mono<PropostaOnline> findTopByEmpresaAndFilial(Integer empresa, Integer filial, Sort sort);

}
