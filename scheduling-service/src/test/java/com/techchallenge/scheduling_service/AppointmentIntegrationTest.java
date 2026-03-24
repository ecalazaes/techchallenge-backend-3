package com.techchallenge.scheduling_service;

import com.techchallenge.scheduling_service.messaging.NotificationProducer;
import com.techchallenge.scheduling_service.model.Appointment;
import com.techchallenge.scheduling_service.repository.AppointmentRepository;
import com.techchallenge.scheduling_service.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class AppointmentIntegrationTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository repository;

    @MockBean // Substitui o Producer real para não tentar conectar no RabbitMQ
    private NotificationProducer producer;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void devePersistirAgendamentoNoBancoDeDados() {
        // GIVEN
        Appointment app = new Appointment();
        app.setPatientName("Erick");
        app.setPatientEmail("erick@teste.com");
        app.setPatientUsername("erick_user");
        app.setDoctorName("Dr. House");
        app.setAppointmentDate(LocalDateTime.now().plusDays(1));

        // WHEN
        Appointment salvo = appointmentService.createAppointment(app);

        // THEN
        Optional<Appointment> buscado = repository.findById(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals("SCHEDULED", buscado.get().getStatus());
        verify(producer, times(1)).sendAppointmentEvent(any());
    }

    @Test
    @DisplayName("Deve mudar status para CANCELLED no banco de dados")
    void deveCancelarConsultaComSucessoNoBanco() {
        // GIVEN: Preencha TODOS os campos obrigatórios
        Appointment app = new Appointment();
        app.setPatientName("Erick");
        app.setPatientEmail("erick@teste.com");
        app.setPatientUsername("erick.calazaes");
        app.setDoctorName("Dr. House");
        app.setAppointmentDate(LocalDateTime.now().plusDays(1));
        app.setStatus("SCHEDULED");

        // Salva o estado inicial
        Appointment salvo = repository.save(app);

        // WHEN: Cancela via Service
        appointmentService.cancelAppointment(salvo.getId());

        // THEN: Busca do banco e verifica
        Appointment cancelado = repository.findById(salvo.getId()).get();
        assertEquals("CANCELLED", cancelado.getStatus());
        verify(producer, times(1)).sendAppointmentEvent(any());
    }
}
