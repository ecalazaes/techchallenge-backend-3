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

    @Test
    void shouldHandleDuplicateMessagesFromRabbitMQ() {
        // 1. Primeira entrega (Criação)
        AppointmentEventDTO dto = new AppointmentEventDTO();
        dto.setId(99L);
        dto.setPatientName("Erick Calazães");
        dto.setDoctorName("Dr. House");
        dto.setPatientEmail("erick@email.com");
        dto.setStatus("AGENDADO");
        dto.setAppointmentDate(LocalDateTime.now()); // <--- Obrigatório

        consumer.receiveAppointmentEvent(dto);

        // 2. Segunda entrega (Simulando repetição com alteração de status)
        // DICA: Não crie um "new AppointmentEventDTO()", apenas mude o campo no objeto existente
        dto.setStatus("COMPLETADO");

        // Agora o dto tem o ID 99 e todos os campos preenchidos, incluindo o appointmentDate
        assertDoesNotThrow(() -> consumer.receiveAppointmentEvent(dto));

        // 3. Verificação final
        MedicalHistory history = repository.findById(99L).orElseThrow();
        assertEquals("COMPLETADO", history.getStatus());
        assertNotNull(history.getAppointmentDate());
    }
}