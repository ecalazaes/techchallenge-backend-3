package com.techchallenge.notification_service.consumer;

import com.techchallenge.notification_service.model.NotificationReminder;
import com.techchallenge.notification_service.repository.NotificationReminderRepository;
import com.techchallenge.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final NotificationReminderRepository repository;
    private final EmailService emailService;

    // Roda a cada 1 hora para verificar quem precisa de lembrete
    @Scheduled(cron = "0/30 * * * * *")
    public void checkAndSendReminders() {
        // Define o limite de 24 horas a partir de agora
        LocalDateTime limiteLembrete = LocalDateTime.now().plusDays(1);

        List<NotificationReminder> pendentes = repository.findByStatusAndAppointmentDateBefore("PENDENTE", limiteLembrete);

        pendentes.forEach(reminder -> {
            emailService.sendReminderEmail(reminder); // Método que você vai criar no seu EmailService
            reminder.setStatus("ENVIADO");
            repository.save(reminder);
        });
    }
}
