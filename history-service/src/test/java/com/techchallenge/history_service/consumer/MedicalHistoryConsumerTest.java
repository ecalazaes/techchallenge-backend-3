package com.techchallenge.history_service.consumer;

import com.techchallenge.history_service.dto.AppointmentEventDTO;
import com.techchallenge.history_service.model.MedicalHistory;
import com.techchallenge.history_service.repository.MedicalHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Valida a sincronização de dados vinda da mensageria para o banco de histórico.
 */
@SpringBootTest
@ActiveProfiles("test")
class MedicalHistoryConsumerTest {

    @Autowired
    private MedicalHistoryConsumer consumer;

    @Autowired
    private MedicalHistoryRepository repository;

    @Test
    void shouldSyncAppointmentEventToHistoryDatabase() {
        AppointmentEventDTO dto = new AppointmentEventDTO();
        dto.setId(99L);
        dto.setPatientName("Paciente Evento");
        dto.setDoctorName("Dr. House");
        dto.setStatus("COMPLETADO");
        dto.setAppointmentDate(LocalDateTime.now());

        consumer.receiveAppointmentEvent(dto);

        MedicalHistory saved = repository.findById(99L).orElseThrow();
        assertEquals("Paciente Evento", saved.getPatientName());
        assertEquals("COMPLETADO", saved.getStatus());
    }
}