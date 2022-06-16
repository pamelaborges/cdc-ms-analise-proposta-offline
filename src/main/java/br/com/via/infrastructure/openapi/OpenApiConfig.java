package br.com.via.infrastructure.openapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Autowired
	private BuildProperties buildProperties;

	@Bean
	public OpenAPI customOpenAPI() {
		final Info info = new Info().title("Documentação de API")
				.description("Documentação de API do Microserviço de analise de proposta offline.")
				.version(this.buildProperties.getVersion());

		return new OpenAPI().info(info);
	}

}
