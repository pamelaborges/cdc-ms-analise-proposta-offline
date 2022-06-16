package br.com.via.application.service;

import br.com.viavarejo.cdc.common.domain.model.analise.AnaliseProposta;
import br.com.viavarejo.cdc.common.domain.model.analise.AnalisePropostaResponse;
import br.com.viavarejo.cdc.common.domain.model.analise.DocumentoVenda;
import br.com.viavarejo.cp.core.domain.model.entity.VvProposalStatus;
import reactor.core.publisher.Mono;

public interface AnalisePropostaService {

	Mono<Boolean> existeProposta(Integer empresa, Integer filial, Integer numero, Integer digito, Integer idf);

	Mono<VvProposalStatus> obterStatusProposta(Integer cliente, Integer codigo, Integer digito, Integer empresa,
			Integer filial);

	Mono<String> obterUiid(Integer codigo, Integer digito, Integer empresa, Integer filial);

	Mono<AnalisePropostaResponse> analisarProposta(AnaliseProposta<DocumentoVenda> analiseProposta);

	Mono<AnalisePropostaResponse> solicitarAnaliseCDCLabRest(AnaliseProposta<DocumentoVenda> analiseProposta);
	
	Mono<AnalisePropostaResponse> enviarParaAnalise(AnaliseProposta<DocumentoVenda> analiseProposta);

	

}
