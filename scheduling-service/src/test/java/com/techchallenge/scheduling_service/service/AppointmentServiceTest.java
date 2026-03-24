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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private NotificationProducer producer;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void deveCriarAgendamentoComStatusScheduledEDispararEvento() {
        // GIVEN
        Appointment appointment = new Appointment();
        appointment.setPatientName("Erick");
        appointment.setDoctorName("Dr. Silva");

        when(repository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        Appointment result = appointmentService.createAppointment(appointment);

        // THEN
        assertEquals("SCHEDULED", result.getStatus());
        verify(repository, times(1)).save(any());
        verify(producer, times(1)).sendConsultationEvent(any());
    }

    @Test
    void deveAtualizarStatusParaCancelledAoCancelar() {
        // GIVEN
        Long id = 1L;
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setStatus("SCHEDULED");

        when(repository.findById(id)).thenReturn(Optional.of(appointment));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        // WHEN
        appointmentService.cancelAppointment(id);

        // THEN
        assertEquals("CANCELLED", appointment.getStatus());
        verify(producer, times(1)).sendConsultationEvent(appointment);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar consulta que não existe")
    void deveFalharAoAtualizarConsultaInexistente() {
        // GIVEN
        Long idInexistente = 999L;
        Appointment dadosNovos = new Appointment();
        dadosNovos.setDoctorName("Dr. Teste");

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            appointmentService.updateAppointment(idInexistente, dadosNovos);
        });

        assertEquals("Agendamento não encontrado com ID: 999", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve permitir agendamentos duplicados para o mesmo médico e hora")
    void naoDevePermitirConflitoDeHorario() {
        // GIVEN
        LocalDateTime horario = LocalDateTime.of(2026, 12, 1, 14, 0);
        String nomeMedico = "Dr. House";

        Appointment app = new Appointment();
        app.setDoctorName(nomeMedico);
        app.setAppointmentDate(horario);
        // ... outros sets ...

        // O PULO DO GATO: Configurar o Mock para simular que já existe um agendamento
        when(repository.existsByDoctorNameAndAppointmentDate(nomeMedico, horario))
                .thenReturn(true);

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            appointmentService.createAppointment(app);
        });
    }
}
