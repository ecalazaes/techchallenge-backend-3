package com.techchallenge.history_service.repository;

import com.techchallenge.history_service.model.MedicalHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de persistência para o repositório de histórico médico.
 * Utiliza {@code @DataJpaTest} para fornecer um ambiente de teste focado apenas
 * nos componentes JPA, utilizando um banco de dados em memória (H2) por padrão.
 * * @author Erick Calazães
 * @version 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
class MedicalHistoryRepositoryTest {

    @Autowired
    private MedicalHistoryRepository repository;

    /**
     * Valida a execução da query customizada para busca de históricos
     * através do nome de usuário do paciente (username).
     */
    @Test
    void deveBuscarHistoricoPorNomeDeUsuario() {
        MedicalHistory history = new MedicalHistory();
        history.setId(5L);
        history.setPatientUsername("user.teste");
        history.setPatientName("Teste");
        history.setDoctorName("Doc");
        history.setAppointmentDate(LocalDateTime.now());
        history.setStatus("PENDENTE");
        repository.save(history);

        List<MedicalHistory> results = repository.findByPatientUsername("user.teste");

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }
}