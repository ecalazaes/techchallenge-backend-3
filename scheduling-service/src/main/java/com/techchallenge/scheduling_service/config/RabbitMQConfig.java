package com.techchallenge.scheduling_service.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Agora usamos um nome que represente uma transmissão para todos
    public static final String FANOUT_EXCHANGE_NAME = "appointment.fanout";

    @Bean
    public FanoutExchange appointmentFanoutExchange() {
        // O Fanout ignora Routing Keys. Ele apenas replica a mensagem para todas as filas ligadas a ele.
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}