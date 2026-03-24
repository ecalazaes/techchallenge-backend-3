package com.techchallenge.notification_service.config;

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

@Configuration
public class RabbitConfig {

    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String FANOUT_EXCHANGE = "appointment.fanout";

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue notificationQueue, FanoutExchange fanoutExchange) {
        // Amarra a fila de notificação ao transmissor geral
        return BindingBuilder.bind(notificationQueue).to(fanoutExchange);
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
}