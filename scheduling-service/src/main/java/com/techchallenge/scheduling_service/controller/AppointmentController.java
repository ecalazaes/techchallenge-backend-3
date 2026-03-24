package com.techchallenge.scheduling_service.controller;

import com.techchallenge.scheduling_service.model.Appointment;
import com.techchallenge.scheduling_service.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // Risco: Estamos recebendo e retornando a Entidade direto.
    // Em um cenário de produção, obrigatoriamente usaríamos um DTO para não expor o banco.
    // Para o nível de aprendizado atual, vamos focar em fazer funcionar.
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment saved = appointmentService.createAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<Appointment> update(@PathVariable Long id, @RequestBody Appointment details) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, details));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
