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

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final EmailService emailService;
    private final NotificationReminderRepository repository;

    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    @Transactional
    public void receiveNotification(AppointmentEventDTO dto) {
        System.out.println("📩 Evento recebido: " + dto.getStatus() + " para " + dto.getPatientEmail());

        // 1. Tenta buscar um lembrete existente (ex: pelo e-mail e data da consulta)
        // Você precisa criar esse método find no seu Repository
        Optional<NotificationReminder> existingReminder = repository.findByPatientEmailAndAppointmentDate(
                dto.getPatientEmail(), dto.getAppointmentDate());

        NotificationReminder reminder;

        if (existingReminder.isPresent()) {
            // Se já existe, vamos atualizar o que já está no banco
            reminder = existingReminder.get();
            System.out.println("🔄 Atualizando lembrete existente para: " + dto.getPatientEmail());
        } else {
            // Se não existe, criamos um novo
            reminder = new NotificationReminder();
            System.out.println("🆕 Criando novo lembrete para: " + dto.getPatientEmail());
        }

        // 2. Seta os dados (independente de ser novo ou update)
        reminder.setPatientEmail(dto.getPatientEmail());
        reminder.setPatientName(dto.getPatientName());
        reminder.setDoctorName(dto.getDoctorName());
        reminder.setAppointmentDate(dto.getAppointmentDate());
        reminder.setStatus("PENDENTE"); // Volta para pendente se foi editado

        // 3. Salva (O Hibernate decidirá entre Insert ou Update com base no ID)
        repository.save(reminder);

        // 4. Envia o e-mail imediato
        emailService.sendAppointmentEmail(dto);
    }
}