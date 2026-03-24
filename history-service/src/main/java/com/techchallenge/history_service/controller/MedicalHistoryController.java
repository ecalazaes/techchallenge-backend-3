package com.techchallenge.history_service.controller;

import com.techchallenge.history_service.model.MedicalHistory;
import com.techchallenge.history_service.repository.MedicalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryRepository repository;

    @QueryMapping
    public List<MedicalHistory> getHistoryByEmail(
            @Argument String patientEmail,
            @Argument Boolean futureOnly
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // 1. Lógica de Segurança: Se for PACIENTE, ele só vê o dele pelo username
        if (role.equals("ROLE_PACIENTE")) {
            List<MedicalHistory> histories = repository.findByPatientUsername(username);

            // Aplica o filtro de futuras no stream caso seja paciente
            if (Boolean.TRUE.equals(futureOnly)) {
                LocalDateTime agora = LocalDateTime.now();
                return histories.stream()
                        .filter(h -> h.getAppointmentDate().isAfter(agora))
                        .toList();
            }
            return histories;
        }

        // 2. Lógica para Médicos/Enfermeiros (Filtro direto no Banco)
        if (Boolean.TRUE.equals(futureOnly)) {
            // Aqui usamos o método que criamos no Repository com @Query ou After
            return repository.findByPatientEmailAndAppointmentDateAfter(patientEmail, LocalDateTime.now());
        }

        return repository.findByPatientEmail(patientEmail);
    }

    @QueryMapping
    public List<MedicalHistory> getAllHistories() {
        return repository.findAll();
    }

    // Mutação para o Médico adicionar anotações na consulta
    @MutationMapping
    @PreAuthorize("hasRole('MEDICO')")
    public MedicalHistory addNotesToHistory(@Argument Long id, @Argument String notes) {
        MedicalHistory history = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Histórico não encontrado para o ID: " + id));

        history.setNotes(notes);
        return repository.save(history);
    }
}
