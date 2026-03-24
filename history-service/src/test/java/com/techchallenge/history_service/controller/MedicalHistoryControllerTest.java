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
 * Teste de Integração da API GraphQL.
 * Valida o contrato do Schema e a segurança baseada em Roles.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@ActiveProfiles("test")
class MedicalHistoryControllerTest {

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Autowired
    private MedicalHistoryRepository repository;

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