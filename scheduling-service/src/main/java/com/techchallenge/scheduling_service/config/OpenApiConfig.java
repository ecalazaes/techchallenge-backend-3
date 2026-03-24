package com.techchallenge.scheduling_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuração global do OpenAPI (Swagger) para o serviço de agendamento.
 * <p>
 * Esta classe define as informações de cabeçalho da documentação interativa da API,
 * permitindo que desenvolvedores e avaliadores visualizem e testem os endpoints
 * diretamente pelo navegador.
 * </p>
 * <p>
 * Acesse a interface em: <code>/swagger-ui.html</code>
 * </p>
 * @author Erick Calazães
 *
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Scheduling Service API")
                        .version("1.0")
                        .description("Serviço responsável pelo agendamento de consultas do Tech Challenge."));
    }
}
