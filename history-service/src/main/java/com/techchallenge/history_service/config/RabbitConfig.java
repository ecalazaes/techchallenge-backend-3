package com.techchallenge.history_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RabbitConfig {

    // 1. Criamos uma fila EXCLUSIVA para o Histórico.
    // Assim, se a Notificação ler a mensagem dela, a nossa continua aqui.
    public static final String HISTORY_QUEUE = "history.queue";
    public static final String FANOUT_EXCHANGE = "appointment.fanout";

    @Bean
    public Queue historyQueue() {
        return new Queue(HISTORY_QUEUE, true);
    }

    // 2. Declaramos o Exchange (o mesmo nome que está no Agendamento)
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    // 3. A "COLA" (Binding): Dizemos ao RabbitMQ para copiar tudo o que
    // cair no Fanout e jogar para dentro da nossa history.queue
    @Bean
    public Binding binding(Queue historyQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(historyQueue).to(fanoutExchange);
    }

    // ATUALIZADO: Agora o conversor entende LocalDateTime
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Registra o módulo para tipos Java 8 (LocalDate, LocalDateTime, etc)
        objectMapper.registerModule(new JavaTimeModule());

        // Evita que a data seja escrita como um array de números [2026, 3, 24...]
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