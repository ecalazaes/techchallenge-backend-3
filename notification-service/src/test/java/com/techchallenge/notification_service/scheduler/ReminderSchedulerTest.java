package com.techchallenge.notification_service.scheduler;

import com.techchallenge.notification_service.consumer.ReminderScheduler;
import com.techchallenge.notification_service.model.NotificationReminder;
import com.techchallenge.notification_service.repository.NotificationReminderRepository;
import com.techchallenge.notification_service.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReminderSchedulerTest {

    @Mock
    private NotificationReminderRepository repository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReminderScheduler reminderScheduler;

    @Test
    void deveEnviarLembretesParaRegistrosPendentes() {
        // GIVEN: Uma lista com 2 lembretes pendentes
        NotificationReminder r1 = new NotificationReminder();
        r1.setPatientEmail("p1@teste.com");

        NotificationReminder r2 = new NotificationReminder();
        r2.setPatientEmail("p2@teste.com");

        when(repository.findByStatusAndAppointmentDateBefore(eq("PENDENTE"), any()))
                .thenReturn(List.of(r1, r2));

        // WHEN: O robô executa a tarefa
        reminderScheduler.checkAndSendReminders();

        // THEN: O e-mail deve ser enviado 2 vezes e o status deve ser atualizado 2 vezes
        verify(emailService, times(2)).sendReminderEmail(any());
        verify(repository, times(2)).save(any());
    }
}
