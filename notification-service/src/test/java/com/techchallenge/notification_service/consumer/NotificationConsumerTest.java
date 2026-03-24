package com.techchallenge.notification_service.consumer;

import com.techchallenge.notification_service.dto.AppointmentEventDTO;
import com.techchallenge.notification_service.model.NotificationReminder;
import com.techchallenge.notification_service.repository.NotificationReminderRepository;
import com.techchallenge.notification_service.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationConsumerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationReminderRepository repository;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Mock
    private JavaMailSender mailSender;

    @Test
    void deveProcessarMensagemDeAgendamentoESalvarNoBanco() {
        // GIVEN: Um DTO de agendamento novo
        AppointmentEventDTO dto = new AppointmentEventDTO();
        dto.setPatientEmail("erick@teste.com");
        dto.setPatientName("Erick");
        dto.setDoctorName("Dr. Silva");
        dto.setAppointmentDate(LocalDateTime.now().plusDays(2));
        dto.setStatus("SCHEDULED");

        // Simula que não existe um lembrete prévio no banco
        when(repository.findByPatientEmailAndAppointmentDate(any(), any())).thenReturn(Optional.empty());

        // WHEN: O Consumer recebe a mensagem
        notificationConsumer.receiveNotification(dto);

        // THEN: Verificamos se ele tentou salvar no banco e enviar o e-mail
        verify(repository, times(1)).save(any(NotificationReminder.class));
        verify(emailService, times(1)).sendAppointmentEmail(dto);
    }
}
