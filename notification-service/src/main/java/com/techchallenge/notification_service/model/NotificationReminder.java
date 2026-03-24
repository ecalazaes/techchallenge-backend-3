package com.techchallenge.notification_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa um lembrete de notificação no banco de dados.
 * <p>
 * Esta classe é utilizada para persistir os dados básicos de um agendamento
 * vindos do microserviço de agendamento (via RabbitMQ), permitindo que o
 * sistema de notificações gerencie o ciclo de vida dos disparos de e-mail.
 * </p>
 * <p>
 * <b>Papel na Arquitetura:</b> Funciona como uma tabela de auditoria e controle
 * para o {@code ReminderScheduler}, garantindo que notificações não sejam
 * duplicadas e permitindo o reenvio em caso de falhas.
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Entity
@Table(name = "reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String patientEmail;
    private String patientName;
    private String doctorName;
    private LocalDateTime appointmentDate;

    /**
     * Estado atual da notificação no sistema.
     * <p>
     * Valores possíveis:
     * <ul>
     * <li><b>PENDENTE:</b> Aguardando o horário ideal para disparo do lembrete.</li>
     * <li><b>ENVIADO:</b> Notificação já encaminhada ao servidor SMTP com sucesso.</li>
     * <li><b>CANCELADO:</b> Agendamento desmarcado, nenhuma ação necessária.</li>
     * </ul>
     * </p>
     */
    private String status;
}
