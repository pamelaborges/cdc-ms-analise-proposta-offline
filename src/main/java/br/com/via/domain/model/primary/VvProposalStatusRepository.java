package br.com.via.domain.model.primary;

import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import br.com.viavarejo.cp.core.domain.model.entity.VvProposalStatus;
import reactor.core.publisher.Mono;

public interface VvProposalStatusRepository extends ReactiveCrudRepository<VvProposalStatus, ObjectId> {

	Mono<VvProposalStatus> findByCdCliAndCdCttppoAndCdCttppoDvrAndCdEmpgcbCttPpoAndCdFilCttPpo(Integer cliente,
			Integer codigo, Integer digito, Integer empresa, Integer filial);
}
