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

/**
 * DataBinder (Controller) GraphQL para gestão de históricos médicos.
 * <p>
 * Esta classe atua como o entrypoint para consultas flexíveis e mutações de
 * histórico. Ela implementa regras de negócio críticas baseadas no perfil do
 * usuário autenticado (RBAC - Role Based Access Control).
 * </p>
 * <p>
 * <b>Segurança Aplicada:</b>
 * <ul>
 * <li><b>PACIENTE:</b> Possui acesso restrito apenas aos seus próprios registros, identificados via username.</li>
 * <li><b>MEDICO/ENFERMEIRO:</b> Podem consultar históricos de terceiros via e-mail e adicionar notas técnicas.</li>
 * </ul>
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Controller
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryRepository repository;

    /**
     * Resolve a query GraphQL para buscar o histórico de um paciente com filtros opcionais.
     * <p>
     * O método aplica automaticamente filtros de segurança: se o usuário logado for um
     * paciente, ele ignora o e-mail fornecido e retorna apenas seus dados. Caso seja
     * staff médico, o filtro por e-mail e data futura é aplicado diretamente no banco.
     * </p>
     *
     * @param patientEmail E-mail do paciente para busca (usado por médicos/enfermeiros).
     * @param futureOnly Se {@code true}, retorna apenas consultas que ainda não ocorreram.
     * @return Lista de {@link MedicalHistory} filtrada conforme as permissões.
     */
    @QueryMapping
    public List<MedicalHistory> getHistoryByEmail(
            @Argument String patientEmail,
            @Argument Boolean futureOnly
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ROLE_PACIENTE")) {
            List<MedicalHistory> histories = repository.findByPatientUsername(username);

            if (Boolean.TRUE.equals(futureOnly)) {
                LocalDateTime agora = LocalDateTime.now();
                return histories.stream()
                        .filter(h -> h.getAppointmentDate().isAfter(agora))
                        .toList();
            }
            return histories;
        }

        if (Boolean.TRUE.equals(futureOnly)) {
            return repository.findByPatientEmailAndAppointmentDateAfter(patientEmail, LocalDateTime.now());
        }

        return repository.findByPatientEmail(patientEmail);
    }

    /**
     * Recupera todos os registros de histórico sem filtros.
     * <p><b>Atenção:</b> Deve ser protegido ou limitado em ambientes de produção.</p>
     * * @return Lista completa de históricos médicos.
     */
    @QueryMapping
    public List<MedicalHistory> getAllHistories() {
        return repository.findAll();
    }

    /**
     * Mutação GraphQL para enriquecimento do histórico com notas médicas.
     * <p>
     * Restrito exclusivamente a usuários com a role <b>MEDICO</b> através da anotação
     * {@link PreAuthorize}.
     * </p>
     *
     * @param id O identificador único do registro de histórico.
     * @param notes Texto contendo as observações ou diagnósticos da consulta.
     * @return O objeto {@link MedicalHistory} atualizado.
     * @throws RuntimeException Caso o ID fornecido não exista na base de dados.
     */
    @MutationMapping
    @PreAuthorize("hasRole('MEDICO')")
    public MedicalHistory addNotesToHistory(@Argument Long id, @Argument String notes) {
        MedicalHistory history = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Histórico não encontrado para o ID: " + id));

        history.setNotes(notes);
        return repository.save(history);
    }
}
