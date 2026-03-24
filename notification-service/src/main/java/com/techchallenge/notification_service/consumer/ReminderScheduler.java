package com.techchallenge.notification_service.consumer;

import com.techchallenge.notification_service.model.NotificationReminder;
import com.techchallenge.notification_service.repository.NotificationReminderRepository;
import com.techchallenge.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Agendador de tarefas responsável pelo monitoramento e disparo de lembretes preventivos.
 * <p>
 * Esta classe utiliza as capacidades de agendamento do Spring ({@link Scheduled})
 * para varrer periodicamente a base de dados em busca de consultas que ocorrerão
 * nas próximas 24 horas e que ainda não foram notificadas.
 * </p>
 * <p>
 * <b>Funcionamento:</b> Atua como um processo em segundo plano (Background Job),
 * garantindo a entrega de notificações mesmo que o evento original de criação
 * do agendamento tenha ocorrido há muito tempo.
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final NotificationReminderRepository repository;
    private final EmailService emailService;

    /**
     * Verifica e envia lembretes para consultas agendadas para as próximas 24 horas.
     * <p>
     * A tarefa é executada conforme a expressão Cron definida (atualmente configurada
     * para rodar a cada 30 segundos para fins de teste/demonstração).
     * </p>
     * <p>
     * <b>Lógica de Seleção:</b> Filtra registros com status 'PENDENTE' cuja data da
     * consulta seja inferior ao limite de 24 horas a partir do instante atual.
     * </p>
     */
    @Scheduled(cron = "0/30 * * * * *")
    public void checkAndSendReminders() {
        LocalDateTime limiteLembrete = LocalDateTime.now().plusDays(1);

        List<NotificationReminder> pendentes = repository.findByStatusAndAppointmentDateBefore("PENDENTE", limiteLembrete);

        pendentes.forEach(reminder -> {
            emailService.sendReminderEmail(reminder);
            reminder.setStatus("ENVIADO");
            repository.save(reminder);
        });
    }
}
