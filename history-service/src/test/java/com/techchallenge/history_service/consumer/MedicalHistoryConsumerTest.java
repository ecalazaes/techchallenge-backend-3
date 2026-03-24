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
 * Testes de integração para o consumidor de histórico médico.
 * Valida a recepção de eventos via mensageria e a correta persistência/sincronização
 * dos dados no banco de dados de histórico.
 * * @author Erick Calazães
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class MedicalHistoryConsumerTest {

    @Autowired
    private MedicalHistoryConsumer consumer;

    @Autowired
    private MedicalHistoryRepository repository;

    /**
     * Valida se um evento de agendamento recebido é corretamente convertido
     * e persistido como um registro de histórico no banco de dados.
     */
    @Test
    void deveSincronizarEventoDeAgendamentoNoBancoDeHistorico() {
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

    /**
     * Teste de idempotência que garante que o sistema lida corretamente com
     * mensagens duplicadas provenientes do RabbitMQ.
     * Verifica se o registro existente é atualizado sem lançar exceções de integridade.
     */
    @Test
    void deveLidarComMensagensDuplicadasDoRabbitMQ() {
        AppointmentEventDTO dto = new AppointmentEventDTO();
        dto.setId(99L);
        dto.setPatientName("Erick Calazães");
        dto.setDoctorName("Dr. House");
        dto.setPatientEmail("erick@email.com");
        dto.setStatus("AGENDADO");
        dto.setAppointmentDate(LocalDateTime.now());

        consumer.receiveAppointmentEvent(dto);

        dto.setStatus("COMPLETADO");

        assertDoesNotThrow(() -> consumer.receiveAppointmentEvent(dto));

        MedicalHistory history = repository.findById(99L).orElseThrow();
        assertEquals("COMPLETADO", history.getStatus());
        assertNotNull(history.getAppointmentDate());
    }
}