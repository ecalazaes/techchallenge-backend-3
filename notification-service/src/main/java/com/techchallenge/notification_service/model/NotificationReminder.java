package com.techchallenge.notification_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    // Status para o robô não enviar o mesmo e-mail duas vezes
    private String status; // PENDENTE, ENVIADO, CANCELADO
}
