package com.techchallenge.history_service.consumer;

import com.techchallenge.history_service.config.RabbitConfig;
import com.techchallenge.history_service.dto.AppointmentEventDTO;
import com.techchallenge.history_service.model.MedicalHistory;
import com.techchallenge.history_service.repository.MedicalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MedicalHistoryConsumer {

    private final MedicalHistoryRepository repository;

    @RabbitListener(queues = RabbitConfig.HISTORY_QUEUE)
    public void receiveAppointmentEvent(AppointmentEventDTO dto) {

        MedicalHistory history = repository.findById(dto.getId())
                .orElse(new MedicalHistory());

        history.setId(dto.getId());
        history.setPatientName(dto.getPatientName());
        history.setPatientEmail(dto.getPatientEmail());
        history.setPatientUsername(dto.getPatientUsername());
        history.setDoctorName(dto.getDoctorName());
        history.setAppointmentDate(dto.getAppointmentDate());
        history.setStatus(dto.getStatus());
        history.setNotes("Integrado via JSON DTO");

        repository.save(history);

        repository.save(history);
        System.out.println("🔄 Sincronização concluída para ID: " + dto.getId() + " com status: " + dto.getStatus());
    }
}