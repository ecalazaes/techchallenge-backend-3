package com.techchallenge.scheduling_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidade que representa um agendamento de consulta médica no sistema.
 * Armazena informações do paciente, do médico e o estado atual do agendamento.
 * * <p>Esta classe é mapeada para a tabela 'tb_appointments' no banco de dados.</p>
 * @author Erick Calazães
 */
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

    /**
     * Estado atual do agendamento.
     * Exemplos de valores: SCHEDULED (Agendado), CANCELLED (Cancelado), COMPLETED (Finalizado).
     */
    @Column(nullable = false)
    private String status;
}
