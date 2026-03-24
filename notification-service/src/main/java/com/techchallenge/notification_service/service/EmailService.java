package com.techchallenge.notification_service.service;

import com.techchallenge.notification_service.dto.AppointmentEventDTO;
import com.techchallenge.notification_service.model.NotificationReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Serviço de mensageria eletrônica responsável pela interface de comunicação com o paciente.
 * <p>
 * Utiliza o protocolo SMTP (via {@link JavaMailSender}) para o disparo de notificações
 * transacionais e lembretes de agenda. As mensagens são formatadas dinamicamente
 * com base no tipo de evento recebido do sistema de agendamento.
 * </p>
 * <p>
 * <b>Funcionalidades:</b>
 * <ul>
 * <li>Confirmação, atualização e cancelamento imediato de consultas.</li>
 * <li>Envio de lembretes preventivos disparados por tarefas agendadas.</li>
 * </ul>
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Dispara um e-mail imediato baseado em eventos de alteração de estado no agendamento.
     * <p>
     * Este método é invocado pelo {@code NotificationConsumer} assim que uma mensagem
     * chega via RabbitMQ. O assunto e o corpo do e-mail são adaptados de acordo
     * com o status presente no DTO.
     * </p>
     *
     * @param dto Objeto contendo os dados do evento de agendamento.
     */
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

    /**
     * Envia um e-mail de lembrete para consultas próximas da data de realização.
     * <p>
     * Diferente do envio imediato, este método foca em reforçar o compromisso
     * agendado, sendo invocado exclusivamente pelo {@code ReminderScheduler}.
     * </p>
     *
     * @param reminder Entidade contendo os dados persistidos do lembrete.
     */
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

    /**
     * Método privado utilitário que encapsula a lógica de envio do {@link SimpleMailMessage}.
     * <p>
     * Centraliza o tratamento de exceções de conexão SMTP para evitar que falhas de rede
     * interrompam o fluxo principal da aplicação.
     * </p>
     *
     * @param to Destinatário do e-mail.
     * @param subject Assunto da mensagem.
     * @param text Corpo da mensagem em texto plano.
     */
    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("noreply@medsenior.com.br");
            mailSender.send(message);
            System.out.println("E-mail disparado com sucesso para: " + to);
        } catch (Exception e) {
            System.err.println("Erro de conexão SMTP: " + e.getMessage());
        }
    }
}