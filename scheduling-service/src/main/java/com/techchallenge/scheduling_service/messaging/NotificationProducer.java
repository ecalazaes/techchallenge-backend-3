package com.techchallenge.scheduling_service.messaging;

import com.techchallenge.scheduling_service.config.RabbitMQConfig;
import com.techchallenge.scheduling_service.dto.AppointmentEventDTO;
import com.techchallenge.scheduling_service.model.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Componente responsável pela produção e envio de mensagens para o broker RabbitMQ.
 * <p>
 * Atua como o intermediário de saída na arquitetura orientada a eventos (Event-Driven),
 * notificando outros microserviços sobre mudanças de estado ou criações de agendamentos.
 * </p>
 * <p>
 * <b>Estratégia de Mensageria:</b> Utiliza uma Exchange do tipo <b>Fanout</b>,
 * garantindo que a mensagem seja replicada para todas as filas conectadas (ex: Notificações, Histórico).
 * </p>
 *
 * @author Erick Calazães
 *
 */
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Converte os dados de um agendamento para um DTO de evento e o publica no RabbitMQ.
     * <p>
     * Este método é disparado após a persistência bem-sucedida de um agendamento,
     * garantindo a consistência eventual entre os serviços.
     * </p>
     * * @param appointment A entidade original do agendamento contendo os dados processados.
     */
    public void sendAppointmentEvent(Appointment appointment) {
        AppointmentEventDTO dto = new AppointmentEventDTO(
                appointment.getId(),
                appointment.getPatientName(),
                appointment.getPatientEmail(),
                appointment.getPatientUsername(),
                appointment.getDoctorName(),
                appointment.getAppointmentDate(),
                appointment.getStatus()
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE_NAME, "", dto);
        System.out.println("DTO enviado via Fanout: " + dto.getPatientName());
    }
}