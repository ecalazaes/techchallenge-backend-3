package com.techchallenge.history_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String notes; // Anotações do médico (pode ser nulo no início)

}
