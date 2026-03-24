package com.techchallenge.scheduling_service.messaging;

import com.techchallenge.scheduling_service.config.RabbitMQConfig;
import com.techchallenge.scheduling_service.dto.AppointmentEventDTO;
import com.techchallenge.scheduling_service.model.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendConsultationEvent(Appointment appointment) {
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