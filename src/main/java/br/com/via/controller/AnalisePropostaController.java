package br.com.via.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.via.application.service.AnalisePropostaService;
import br.com.viavarejo.cdc.common.domain.model.analise.AnaliseProposta;
import br.com.viavarejo.cdc.common.domain.model.analise.AnalisePropostaResponse;
import br.com.viavarejo.cdc.common.domain.model.analise.DocumentoVenda;
import br.com.viavarejo.cdc.common.infrastructure.tracking.TrackingInput;
import br.com.viavarejo.cdc.common.infrastructure.tracking.TrackingLogger;
import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("analise-proposta/v2")
public class AnalisePropostaController {

	@Autowired
	private AnalisePropostaService analisePropostaService;

	@PostMapping
	@Operation(summary = "Análise de proposta de crédito offline.")
	@TrackingLogger
	public Mono<ResponseEntity<AnalisePropostaResponse>> analiseProposta(
			@TrackingInput("request") @RequestBody AnaliseProposta<DocumentoVenda> analiseProposta) {
		return analisePropostaService.analisarProposta(analiseProposta).map(retorno -> {
			if (retorno.getRetCod() > 0) {
				return new ResponseEntity<AnalisePropostaResponse>(retorno, HttpStatus.BAD_REQUEST);
			} else {
				return ResponseEntity.ok(retorno);

			}
		}
		);

	}

}
