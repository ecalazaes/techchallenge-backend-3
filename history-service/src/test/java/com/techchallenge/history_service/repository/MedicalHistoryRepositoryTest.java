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
 * Teste focado apenas na camada de persistência.
 * Usa @DataJpaTest para carregar apenas os componentes do Hibernate/JPA.
 */
@DataJpaTest
@ActiveProfiles("test")
class MedicalHistoryRepositoryTest {

    @Autowired
    private MedicalHistoryRepository repository;

    @Test
    void shouldFindHistoryByUsername() {
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