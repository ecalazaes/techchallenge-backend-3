package com.techchallenge.scheduling_service.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuração de mensageria utilizando RabbitMQ para o serviço de agendamento.
 * <p>
 * Define os componentes necessários para a comunicação assíncrona entre microserviços,
 * permitindo que o <code>Scheduling Service</code> notifique outros serviços (como Notification e History)
 * sobre eventos de consultas.
 * </p>
 *
 * @author Erick Calazães
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Nome da Exchange do tipo Fanout utilizada para transmitir eventos de agendamento.
     */
    public static final String FANOUT_EXCHANGE_NAME = "appointment.fanout";

    /**
     * Define a Exchange do tipo Fanout.
     * <p>
     * O tipo <b>Fanout</b> foi escolhido porque ignora chaves de roteamento (Routing Keys)
     * e replica a mensagem para todas as filas que estiverem ligadas (binded) a ela.
     * Isso garante que múltiplos serviços interessados recebam a mesma notificação simultaneamente.
     * </p>
     *
     * @return Uma instância de {@link FanoutExchange} configurada.
     */
    @Bean
    public FanoutExchange appointmentFanoutExchange() {
        // O Fanout ignora Routing Keys. Ele apenas replica a mensagem para todas as filas ligadas a ele.
        return new FanoutExchange(FANOUT_EXCHANGE_NAME);
    }

    /**
     * Configura o conversor de mensagens para utilizar o formato JSON.
     * <p>
     * Garante que os objetos Java enviados para as filas sejam serializados como JSON
     * e desserializados corretamente pelos serviços consumidores.
     * </p>
     *
     * @return Um {@link MessageConverter} do tipo Jackson2Json.
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}