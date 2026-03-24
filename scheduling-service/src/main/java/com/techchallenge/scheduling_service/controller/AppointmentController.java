package com.techchallenge.scheduling_service.controller;

import com.techchallenge.scheduling_service.model.Appointment;
import com.techchallenge.scheduling_service.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável por gerenciar os endpoints de agendamento de consultas.
 * <p>
 * Centraliza as operações de criação, atualização e cancelamento de agendamentos,
 * integrando a lógica de negócio com as regras de segurança e mensageria.
 * </p>
 * @author Erick Calazães
 *
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Agendamentos", description = "Operações para gestão de consultas médicas")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Criar agendamento", description = "Registra uma nova consulta e dispara notificações para os serviços de histórico e notificação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou conflito de horário")
    })
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment saved = appointmentService.createAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Atualizar agendamento", description = "Modifica os dados de uma consulta existente. Requer perfil MEDICO ou ENFERMEIRO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado para o perfil do usuário"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<Appointment> update(@PathVariable Long id, @RequestBody Appointment details) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, details));
    }

    @Operation(summary = "Cancelar agendamento", description = "Remove um agendamento da base de dados. Requer perfil MEDICO ou ENFERMEIRO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Agendamento cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "ID informado não existe")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
