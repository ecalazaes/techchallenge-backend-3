package com.techchallenge.history_service.repository;

import com.techchallenge.history_service.model.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByPatientEmail(String patientEmail);

    List<MedicalHistory> findByPatientUsername(String patientUsername);

    // Query para buscar apenas consultas futuras de um paciente específico
    List<MedicalHistory> findByPatientEmailAndAppointmentDateAfter(String patientEmail, LocalDateTime date);
}
