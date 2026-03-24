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

/**
 * Configuração de infraestrutura para mensageria utilizando RabbitMQ.
 * <p>
 * Esta classe define os componentes necessários para que o serviço de notificação
 * possa consumir mensagens de agendamento, estabelecendo a fila, a exchange e
 * a estratégia de conversão de dados.
 * </p>
 * <p>
 * <b>Destaque:</b> Inclui suporte nativo para tipos de data do Java 8+ através do
 * {@link JavaTimeModule}, essencial para o processamento de horários de consultas.
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
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
        return BindingBuilder.bind(notificationQueue).to(fanoutExchange);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter(objectMapper);
    }
}