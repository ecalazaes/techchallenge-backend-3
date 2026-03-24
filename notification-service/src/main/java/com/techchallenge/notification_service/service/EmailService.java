package com.techchallenge.notification_service.service;

import com.techchallenge.notification_service.dto.AppointmentEventDTO;
import com.techchallenge.notification_service.model.NotificationReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Método para o CONSUMER (Imediato)
    public void sendAppointmentEmail(AppointmentEventDTO dto) {
        String formattedDate = dto.getAppointmentDate().format(formatter);
        String subject;
        String messageTitle;

        switch (dto.getStatus()) {
            case "UPDATED_SCHEDULED":
                subject = "Consulta Atualizada - MedSênior";
                messageTitle = "Sua consulta foi alterada com sucesso!";
                break;
            case "CANCELLED":
                subject = "Consulta Cancelada - MedSênior";
                messageTitle = "Sua consulta foi cancelada conforme solicitado.";
                break;
            default:
                subject = "Confirmação de Agendamento - MedSênior";
                messageTitle = "Seu agendamento foi realizado com sucesso!";
                break;
        }

        String body = String.format(
                "Olá %s,\n\n%s\n\nDetalhes:\nMédico: %s\nData: %s\nStatus: %s\n\nObrigado por confiar na MedSênior.",
                dto.getPatientName(), messageTitle, dto.getDoctorName(), formattedDate, dto.getStatus()
        );

        sendEmail(dto.getPatientEmail(), subject, body);
    }

    // Método para o SCHEDULER (Lembrete Futuro)
    public void sendReminderEmail(NotificationReminder reminder) {
        String formattedDate = reminder.getAppointmentDate().format(formatter);

        String subject = "LEMBRETE: Sua consulta é amanhã! - MedSênior";
        String body = String.format(
                "Olá %s,\n\nEste é um lembrete amigável. Sua consulta está chegando!\n\n" +
                        "Médico: %s\n" +
                        "Data: %s\n\nCaso não possa comparecer, por favor cancele pelo portal.",
                reminder.getPatientName(), reminder.getDoctorName(), formattedDate
        );

        sendEmail(reminder.getPatientEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("noreply@medsenior.com.br");
            mailSender.send(message);
            System.out.println("📧 E-mail disparado com sucesso para: " + to);
        } catch (Exception e) {
            System.err.println("❌ Erro de conexão SMTP: " + e.getMessage());
        }
    }
}