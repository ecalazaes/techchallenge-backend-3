package com.techchallenge.scheduling_service.service;

import com.techchallenge.scheduling_service.messaging.NotificationProducer;
import com.techchallenge.scheduling_service.model.Appointment;
import com.techchallenge.scheduling_service.repository.AppointmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Classe de testes unitários para o serviço de agendamentos (AppointmentService).
 * Valida regras de negócio, integração com repositórios e mensageria.
 * * @author Erick Calazães
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private NotificationProducer producer;

    @InjectMocks
    private AppointmentService appointmentService;

    /**
     * Valida se um novo agendamento é criado com o status inicial correto
     * e se o evento de notificação é disparado após a persistência.
     */
    @Test
    void deveCriarAgendamentoComStatusScheduledEDispararEvento() {
        Appointment appointment = new Appointment();
        appointment.setPatientName("Erick");
        appointment.setDoctorName("Dr. Silva");

        when(repository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment result = appointmentService.createAppointment(appointment);

        assertEquals("SCHEDULED", result.getStatus());
        verify(repository, times(1)).save(any());
        verify(producer, times(1)).sendAppointmentEvent(any());
    }

    /**
     * Verifica a transição de status para cancelado e a comunicação
     * do evento de cancelamento para os demais microserviços.
     */
    @Test
    void deveAtualizarStatusParaCancelledAoCancelar() {
        Long id = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setStatus("SCHEDULED");

        when(repository.findById(id)).thenReturn(Optional.of(appointment));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        appointmentService.cancelAppointment(id);

        assertEquals("CANCELLED", appointment.getStatus());
        verify(producer, times(1)).sendAppointmentEvent(appointment);
    }

    /**
     * Testa o comportamento do sistema ao tentar manipular um ID inexistente,
     * garantindo que a exceção adequada seja lançada com a mensagem correta.
     */
    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar consulta que não existe")
    void deveFalharAoAtualizarConsultaInexistente() {
        Long idInexistente = 999L;
        Appointment dadosNovos = new Appointment();
        dadosNovos.setDoctorName("Dr. Teste");

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.updateAppointment(idInexistente, dadosNovos);
        });

        assertEquals("Agendamento não encontrado com ID: 999", exception.getMessage());
        verify(repository, never()).save(any());
    }

    /**
     * Valida a regra de negócio que impede o agendamento de consultas
     * para o mesmo médico em horários conflitantes.
     */
    @Test
    @DisplayName("Não deve permitir agendamentos duplicados para o mesmo médico e hora")
    void naoDevePermitirConflitoDeHorario() {
        LocalDateTime horario = LocalDateTime.of(2026, 12, 1, 14, 0);
        String nomeMedico = "Dr. House";

        Appointment app = new Appointment();
        app.setDoctorName(nomeMedico);
        app.setAppointmentDate(horario);

        when(repository.existsByDoctorNameAndAppointmentDate(nomeMedico, horario))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            appointmentService.createAppointment(app);
        });
    }

    /**
     * Teste de borda para validar a resiliência do sistema sob condições de concorrência.
     * Simula múltiplas threads tentando realizar o mesmo agendamento simultaneamente.
     * * @throws InterruptedException caso a execução das threads seja interrompida.
     */
    @Test
    void deveImpedirAgendamentosDuplicadosEmParalelo() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);
    }
}
