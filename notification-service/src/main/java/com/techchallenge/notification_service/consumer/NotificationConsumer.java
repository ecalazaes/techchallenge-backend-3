package com.techchallenge.notification_service.consumer;

import com.techchallenge.notification_service.config.RabbitConfig;
import com.techchallenge.notification_service.dto.AppointmentEventDTO;
import com.techchallenge.notification_service.model.NotificationReminder;
import com.techchallenge.notification_service.repository.NotificationReminderRepository;
import com.techchallenge.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Consumidor de mensagens responsável por processar eventos de agendamento.
 * <p>
 * Esta classe atua como o entrypoint para as mensagens vindas do RabbitMQ.
 * Ela coordena a lógica de negócio necessária para manter o histórico de notificações
 * sincronizado e acionar o serviço de envio de e-mails.
 * </p>
 * <p>
 * <b>Idempotência:</b> O método de recepção foi projetado para lidar com reprocessamentos,
 * verificando a existência de lembretes prévios antes de criar novos registros.
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;
    private final NotificationReminderRepository repository;

    /**
     * Intercepta mensagens da fila de notificações e processa o ciclo de vida do lembrete.
     * <p>
     * O fluxo consiste em:
     * <ol>
     * <li>Consumir o DTO serializado em JSON da fila {@code notification.queue}.</li>
     * <li>Sincronizar o estado do agendamento no banco de dados local (Upsert).</li>
     * <li>Disparar a notificação imediata via protocolo SMTP.</li>
     * </ol>
     * </p>
     * <p>
     * <b>Transacionalidade:</b> O método é anotado com {@link Transactional} para garantir
     * que a persistência no banco de dados seja atômica.
     * </p>
     *
     * @param dto Objeto de transferência contendo os detalhes do agendamento (Paciente, Médico, Data).
     */
    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    @Transactional
    public void receiveNotification(AppointmentEventDTO dto) {
        System.out.println("Evento recebido: " + dto.getStatus() + " para " + dto.getPatientEmail());

        Optional<NotificationReminder> existingReminder = repository.findByPatientEmailAndAppointmentDate(
                dto.getPatientEmail(), dto.getAppointmentDate());

        NotificationReminder reminder;

        if (existingReminder.isPresent()) {
            reminder = existingReminder.get();
            System.out.println("Atualizando lembrete existente para: " + dto.getPatientEmail());
        } else {
            reminder = new NotificationReminder();
            System.out.println("Criando novo lembrete para: " + dto.getPatientEmail());
        }

        reminder.setPatientEmail(dto.getPatientEmail());
        reminder.setPatientName(dto.getPatientName());
        reminder.setDoctorName(dto.getDoctorName());
        reminder.setAppointmentDate(dto.getAppointmentDate());
        reminder.setStatus("PENDENTE");

        repository.save(reminder);

        emailService.sendAppointmentEmail(dto);
    }
}