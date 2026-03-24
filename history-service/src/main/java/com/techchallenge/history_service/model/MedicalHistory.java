package com.techchallenge.history_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidade que representa o registro histórico de uma consulta médica.
 * <p>
 * Esta classe é o modelo de dados central do serviço de histórico, sendo populada
 * de forma assíncrona através de eventos de mensageria. Ela armazena uma cópia
 * denormalizada dos dados de agendamento para otimizar consultas de leitura e auditoria.
 * </p>
 * <p>
 * <b>Nota de Design:</b> O identificador {@code id} não é gerado localmente; ele
 * espelha o ID original do agendamento proveniente do sistema de origem para
 * manter a integridade referencial entre microserviços.
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Entity
@Table(name = "tb_medical_histories")
@Data
public class MedicalHistory {

    @Id
    private Long id;

    @Column(nullable = false)
    private String patientName;

    @Column
    private String patientEmail;

    @Column
    private String patientUsername;

    @Column(nullable = false)
    private String doctorName;

    @Column(nullable = false)
    private LocalDateTime appointmentDate;

    /** * Estado atual da consulta (ex: AGENDADO, CONCLUIDO, CANCELADO).
     * Reflete o último evento recebido via mensageria.
     */
    @Column(nullable = false)
    private String status;

    /** * Campo de texto livre para observações, diagnósticos ou notas técnicas.
     * Este campo é tipicamente editado via mutações GraphQL pelo corpo médico.
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

}
