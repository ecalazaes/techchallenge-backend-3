package com.techchallenge.history_service.controller;

import com.techchallenge.history_service.model.MedicalHistory;
import com.techchallenge.history_service.repository.MedicalHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

/**
 * Testes de integração para a API GraphQL do histórico médico.
 * Valida a execução de Queries e Mutations, o cumprimento do Schema GraphQL
 * e a aplicação de segurança baseada em perfis de acesso (Roles).
 * * @author Erick Calazães
 * @version 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("test")
class MedicalHistoryControllerTest {

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Autowired
    private MedicalHistoryRepository repository;

    /**
     * Configuração inicial para cada teste.
     * Limpa o repositório e insere um registro base para validação das consultas.
     */
    @BeforeEach
    void setup() {
        repository.deleteAll();
        MedicalHistory history = new MedicalHistory();
        history.setId(1L);
        history.setPatientName("Erick Teste");
        history.setPatientUsername("erick.user");
        history.setPatientEmail("erick@teste.com");
        history.setDoctorName("Dr. Teste");
        history.setAppointmentDate(LocalDateTime.now().plusDays(1));
        history.setStatus("AGENDADO");
        repository.save(history);
    }

    /**
     * Valida se um usuário autenticado com o perfil de PACIENTE consegue
     * buscar seu próprio histórico através de uma Query GraphQL.
     */
    @Test
    @WithMockUser(username = "erick.user", roles = {"PACIENTE"})
    void shouldFetchHistoryForAuthenticatedUser() {
        String query = """
            query {
              getHistoryByEmail(patientEmail: "erick@teste.com") {
                patientName
                status
              }
            }
        """;

        graphQlTester.document(query)
                .execute()
                .path("getHistoryByEmail[0].patientName")
                .entity(String.class)
                .isEqualTo("Erick Teste");
    }

    /**
     * Valida se um usuário com o perfil de MEDICO possui permissão para
     * atualizar as observações de um histórico através de uma Mutation GraphQL.
     */
    @Test
    @WithMockUser(roles = {"MEDICO"})
    void shouldUpdateNotesViaMutation() {
        String mutation = """
            mutation {
              addNotesToHistory(id: 1, notes: "Nova nota do medico") {
                notes
              }
            }
        """;

        graphQlTester.document(mutation)
                .execute()
                .path("addNotesToHistory.notes")
                .entity(String.class)
                .isEqualTo("Nova nota do medico");
    }
}