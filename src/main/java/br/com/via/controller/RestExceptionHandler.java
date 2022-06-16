package br.com.via.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.viavarejo.cdc.common.exceptions.BusinessException;
import br.com.viavarejo.cdc.common.exceptions.NoContentException;
import br.com.viavarejo.cdc.common.exceptions.RestErrorDto;

/**
 * Spring WebFlux REST API exception handler.
 * 
 * Main reference: https://hantsy.github.io/spring-reactive-sample/web/exception.html
 * 
 * @author falvojr
 */
@RestControllerAdvice
public class RestExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

	@ExceptionHandler(NoContentException.class)
	private ResponseEntity<Void> noContentExceptionHandler() {
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(BusinessException.class)
	private ResponseEntity<RestErrorDto> businessExceptionHandler(BusinessException businessException) {
		final String businessMessage = businessException.getMessage();
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new RestErrorDto(businessMessage));
	}

	@ExceptionHandler(Throwable.class)
	private ResponseEntity<RestErrorDto> unexpectedExceptionHandler(Throwable unexpectedException) {
		final String unexpectedErrorMessage = "Ocorreu um erro inesperado.";
		log.error(unexpectedErrorMessage, unexpectedException);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RestErrorDto(unexpectedErrorMessage));
	}
}
