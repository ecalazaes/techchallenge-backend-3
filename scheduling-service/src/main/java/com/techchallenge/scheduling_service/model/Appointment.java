package com.techchallenge.scheduling_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_appointments")
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientName;

    @Column(nullable = false)
    private String patientEmail;

    @Column(nullable = false)
    private String patientUsername;

    @Column(nullable = false)
    private String doctorName;

    @Column(nullable = false)
    private LocalDateTime appointmentDate;

    @Column(nullable = false)
    private String status; // Ex: AGENDADA, REALIZADA, CANCELADA

    // Dica para o envio ao RabbitMQ depois:
    // Evite mapear relacionamentos complexos (ManyToOne) agora se não for estritamente necessário.
}
