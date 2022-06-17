package br.com.via.application.service.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.via.application.service.AnalisePropostaService;
import br.com.via.application.service.KafkaService;
import br.com.via.domain.model.primary.VvProposalAnalysisRepository;
import br.com.via.domain.model.primary.VvProposalStatusRepository;
import br.com.via.infrastructure.siteproperties.SitePropertiesSource;
import br.com.viavarejo.cdc.common.domain.model.analise.AnaliseProposta;
import br.com.viavarejo.cdc.common.domain.model.analise.AnalisePropostaResponse;
import br.com.viavarejo.cdc.common.domain.model.analise.DocumentoVenda;
import br.com.viavarejo.cdc.common.exceptions.BusinessException;
import br.com.viavarejo.cdc.common.infrastructure.tracking.AbstractTrackingLoggerAspect;
import br.com.viavarejo.cp.core.domain.model.entity.VvProposalAnalysis;
import br.com.viavarejo.cp.core.domain.model.entity.VvProposalStatus;
import br.com.viavarejo.cp.model.StatusProposta;
import br.com.viavarejo.cp.model.kafka.AnalisarPropostaCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class AnalisePropostaServiceImpl implements AnalisePropostaService {

	private static final Logger log = LoggerFactory.getLogger(AnalisePropostaService.class);

	@Autowired
	private VvProposalAnalysisRepository vvProposalAnalysisRepository;

	@Autowired
	private VvProposalStatusRepository vvProposalStatusRepository;

	@Autowired
	private KafkaService<AnalisarPropostaCommand> kafkaService;

	@Autowired
	private WebClient.Builder webClient;

	@Value("${analise-proposta.kafka.producer.topic}")
	private String kafkaProducerTopicCommandAnalisarProposta;

	@Value("${analise-proposta.services.analise-proposta-offline-cdclab}")
	private String analisePropostaCDCLabServiceURL;

	@Autowired
	private Validator validator;

	@Autowired
	private SitePropertiesSource sitePropertiesSource;

	@Override
	public Mono<Boolean> existeProposta(Integer empresa, Integer filial, Integer numero, Integer digito, Integer idf) {
		final Mono<Long> quantidade = vvProposalAnalysisRepository.countPropostaPor(empresa, filial, numero, digito,
				idf);
		return quantidade.map(qtd -> {
			return qtd > 0;
		});
	}

	@Override
	public Mono<VvProposalStatus> obterStatusProposta(Integer cliente, Integer codigo, Integer digito, Integer empresa,
			Integer filial) {
		return vvProposalStatusRepository.findByCdCliAndCdCttppoAndCdCttppoDvrAndCdEmpgcbCttPpoAndCdFilCttPpo(cliente,
				codigo, digito, empresa, filial);
	}

	@Override
	public Mono<String> obterUiid(Integer codigo, Integer digito, Integer empresa, Integer filial) {
		final Flux<VvProposalAnalysis> proposalAnalysis = this.vvProposalAnalysisRepository.obterUiid(codigo, digito,
				empresa, filial);

		return proposalAnalysis.map(VvProposalAnalysis::getUuid).single();

	}

	@Override
	public Mono<AnalisePropostaResponse> analisarProposta(AnaliseProposta<DocumentoVenda> analiseProposta) {

		final AnalisePropostaResponse response = new AnalisePropostaResponse();

		final Set<ConstraintViolation<AnaliseProposta<DocumentoVenda>>> violations = validator
				.validate(analiseProposta);
		if (violations.size() > 0) {
			final StringBuilder builder = new StringBuilder();
			for (ConstraintViolation<AnaliseProposta<DocumentoVenda>> error : violations) {
				builder.append(String.format("%s: %s (value='%s') | ", error.getPropertyPath(), error.getMessage(),
						error.getInvalidValue()));
			}
			final String msgCod = String.format("Erro na validacao dos dados basicos do servico. [%s]",
					builder.toString());

			response.setMsgCod(msgCod);
			response.setRetCod(13);
			return Mono.just(response);
		}

		final Integer cliente = analiseProposta.getCdCli();
		final Integer codigo = analiseProposta.getCdCttppo();
		final Integer digito = analiseProposta.getCdCttppoDvr();
		final Integer empresa = analiseProposta.getCdEmpgcbCttPpo();
		final Integer filial = analiseProposta.getCdFilCttPpo();
		final Integer idf = analiseProposta.getCdCclogIdf();

		Mono<Boolean> existeProposta = this.existeProposta(empresa, filial, codigo, digito, idf);
		Mono<VvProposalStatus> monoVvStatusProposta = this.obterStatusProposta(cliente, codigo, digito, empresa,
				filial);

		return Mono.zip(existeProposta, monoVvStatusProposta).flatMap(tuple -> {
			final boolean isExisteProposta = tuple.getT1();
			final VvProposalStatus vvStatusProposta = tuple.getT2();

			final StatusProposta statusProposta = nonNull(vvStatusProposta)
					? StatusProposta.getByCode(vvStatusProposta.getStatus().getCdStatus())
					: null;

			if (isExisteProposta) {
				if (nonNull(statusProposta) && statusProposta.equals(StatusProposta.PROCESSANDO)) {

					response.setRetCod(55);
					response.setMsgCod("Proposta existente!");
					return Mono.just(response);
				}

				final boolean isRessubmissao = (idf > 1);
				if (isRessubmissao && !(StatusProposta.AGUARDANDO_ALTERACAO.equals(statusProposta)
						|| StatusProposta.AGUARDANDO_PAINEL.equals(statusProposta))) {
					response.setRetCod(56);
					response.setMsgCod("Proposta Não Alterada!");
					return Mono.just(response);
				}
			}

			String uuid = this.obterUiid(codigo, digito, empresa, filial).block();

			if (isNull(uuid)) {
				uuid = AbstractTrackingLoggerAspect.getCurrentUuid();
			}

			final VvProposalAnalysis vvProposalAnalysis = new VvProposalAnalysis();
			vvProposalAnalysis.setCdFilCttPpo(filial);
			vvProposalAnalysis.setCdEmpgcbCttPpo(empresa);
			vvProposalAnalysis.setCdCttppo(codigo);
			vvProposalAnalysis.setCdCttppoDvr(digito);
			vvProposalAnalysis.setCdCclogIdf(idf);
			vvProposalAnalysis.setCdCli(cliente);
			vvProposalAnalysis.setCdEpsrv(analiseProposta.getCdEpsrv());
			vvProposalAnalysis.setUuid(uuid);
			vvProposalAnalysis.setRequest(new Document("proposta", analiseProposta));
			vvProposalAnalysisRepository.save(vvProposalAnalysis);

			this.sendToKafka(analiseProposta).block();

			response.setMsgCod("");
			response.setRetCod(0);

			return Mono.just(response);

		});
	}

	@Override
	public Mono<AnalisePropostaResponse> solicitarAnaliseCDCLabRest(AnaliseProposta<DocumentoVenda> analiseProposta) {
		log.debug("Integração Kafka desativada. Solicitando análise diretamente ao CDCLab.");

		return this.webClient.baseUrl(this.analisePropostaCDCLabServiceURL).build().post()
				.accept(MediaType.APPLICATION_JSON).bodyValue(analiseProposta)
				.exchangeToMono(clientResponse -> clientResponse.bodyToMono(AnalisePropostaResponse.class));
	}

	private Mono<AnaliseProposta<DocumentoVenda>> sendToKafka(AnaliseProposta<DocumentoVenda> analiseProposta) {

		return kafkaService
				.send(this.kafkaProducerTopicCommandAnalisarProposta, this.buildCommandToKafka(analiseProposta))
				.flatMap(senderResult -> Mono.just(analiseProposta)).onErrorMap(error -> {
					final String message = "Operação cancelada devido ao não envio para o Kafka.";
					log.warn(message, error);
					return new BusinessException(message);
				});
	}

	private AnalisarPropostaCommand buildCommandToKafka(AnaliseProposta<DocumentoVenda> analiseProposta) {
		final AnalisarPropostaCommand command = new AnalisarPropostaCommand();
		final Integer codigo = analiseProposta.getCdCttppo();
		final Integer digito = analiseProposta.getCdCttppoDvr();
		final Integer empresa = analiseProposta.getCdEmpgcbCttPpo();
		final Integer filial = analiseProposta.getCdFilCttPpo();
		final Integer idf = analiseProposta.getCdCclogIdf();

		command.setCodigo(codigo);
		command.setDigito(digito);
		command.setEmpresa(empresa);
		command.setFilial(filial);
		command.setIdf(idf);

		return command;
	}

	@Override
	public Mono<AnalisePropostaResponse> enviarParaAnalise(AnaliseProposta<DocumentoVenda> analiseProposta) {
		final Mono<Boolean> isMicroservicosKafkaHabilitado = this.sitePropertiesSource
				.isMicrosservicosKafkaHabilitado();
		final Mono<Boolean> isMsAnalisePropostaOfflineKafkaHabilitado = this.sitePropertiesSource
				.isMsAnalisePropostaOfflineKafkaHabilitado();

		final Mono<Tuple2<Boolean, Boolean>> zipConditions = Mono.zip(isMicroservicosKafkaHabilitado,
				isMsAnalisePropostaOfflineKafkaHabilitado);

		return zipConditions.flatMap(tuple -> {

			System.out.println("Oiii");

			final boolean isMicroservicosKafkaHabilitadoBoolean = tuple.getT1();
			final boolean isMsAnalisePropostaOfflineKafkaHabilitadoBoolean = tuple.getT2();

			if (isMicroservicosKafkaHabilitadoBoolean && isMsAnalisePropostaOfflineKafkaHabilitadoBoolean) {
				return this.analisarProposta(analiseProposta);
			} else {
				return this.solicitarAnaliseCDCLabRest(analiseProposta);
			}

		});
	}

}
