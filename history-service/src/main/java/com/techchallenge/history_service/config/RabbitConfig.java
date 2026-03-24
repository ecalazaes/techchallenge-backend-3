package com.techchallenge.history_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de infraestrutura para mensageria RabbitMQ e conectividade Web (CORS).
 * <p>
 * Esta classe estabelece a topologia de rede necessária para o consumo de eventos
 * de agendamento, garantindo que o serviço de histórico possua sua própria fila
 * persistente dentro de uma arquitetura baseada em <b>Fanout Exchange</b>.
 * </p>
 * <p>
 * Adicionalmente, configura as políticas de CORS para permitir a exploração da
 * interface GraphiQL durante o desenvolvimento.
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Configuration
public class RabbitConfig {

    public static final String HISTORY_QUEUE = "history.queue";
    public static final String HISTORY_DLQ = "history.queue.dlq";
    public static final String FANOUT_EXCHANGE = "appointment.fanout";

    @Bean
    public Queue historyQueue() {
        return QueueBuilder.durable(HISTORY_QUEUE)
                // Se a mensagem for rejeitada após as tentativas, vai para a DLQ
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", HISTORY_DLQ)
                .build();
    }

    @Bean
    public Queue historyDLQ() {
        return new Queue(HISTORY_DLQ, true);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue historyQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(historyQueue).to(fanoutExchange);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/graphql").allowedOrigins("*");
                registry.addMapping("/graphiql").allowedOrigins("*");
            }
        };
    }
}