package com.techchallenge.history_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentEventDTO implements Serializable {
    private Long id;

    @NotBlank(message = "O nome do paciente é obrigatório")
    private String patientName;

    @NotBlank(message = "O e-mail do paciente é obrigatório")
    @Email(message = "O e-message do paciente deve ser um endereço de e-mail válido")
    private String patientEmail;

    @NotBlank(message = "O username do paciente é obrigatório")
    private String patientUsername;

    @NotBlank(message = "O nome do médico é obrigatório")
    private String doctorName;

    @NotNull(message = "A data da consulta é obrigatória")
    @Future(message = "A data da consulta deve ser uma data futura")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appointmentDate;

    @NotBlank(message = "O status da consulta é obrigatório")
    private String status;

}