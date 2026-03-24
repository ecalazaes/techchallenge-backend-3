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

/**
 * Testes de integração para o fluxo de agendamentos.
 * Valida a persistência real no banco de dados H2 e a integração entre Service e Repository,
 * utilizando MockBean para isolar a camada de mensageria.
 * * @author Erick Calazães
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class AppointmentIntegrationTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository repository;

    @MockBean
    private NotificationProducer producer;

    /**
     * Limpa o banco de dados antes de cada execução de teste para garantir o isolamento.
     */
    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    /**
     * Valida o ciclo de vida completo da criação de um agendamento, verificando a
     * persistência correta no banco e o disparo da integração por mensagem.
     */
    @Test
    void devePersistirAgendamentoNoBancoDeDados() {
        Appointment app = new Appointment();
        app.setPatientName("Erick");
        app.setPatientEmail("erick@teste.com");
        app.setPatientUsername("erick_user");
        app.setDoctorName("Dr. House");
        app.setAppointmentDate(LocalDateTime.now().plusDays(1));

        Appointment salvo = appointmentService.createAppointment(app);

        Optional<Appointment> buscado = repository.findById(salvo.getId());
        assertTrue(buscado.isPresent());
        assertEquals("SCHEDULED", buscado.get().getStatus());
        verify(producer, times(1)).sendAppointmentEvent(any());
    }

    /**
     * Verifica se a operação de cancelamento atualiza corretamente o estado da
     * entidade no banco de dados e notifica os serviços interessados.
     */
    @Test
    @DisplayName("Deve mudar status para CANCELLED no banco de dados")
    void deveCancelarConsultaComSucessoNoBanco() {
        Appointment app = new Appointment();
        app.setPatientName("Erick");
        app.setPatientEmail("erick@teste.com");
        app.setPatientUsername("erick.calazaes");
        app.setDoctorName("Dr. House");
        app.setAppointmentDate(LocalDateTime.now().plusDays(1));
        app.setStatus("SCHEDULED");

        Appointment salvo = repository.save(app);

        appointmentService.cancelAppointment(salvo.getId());

        Appointment cancelado = repository.findById(salvo.getId()).get();
        assertEquals("CANCELLED", cancelado.getStatus());
        verify(producer, times(1)).sendAppointmentEvent(any());
    }
}
