package com.techchallenge.scheduling_service.repository;

import com.techchallenge.scheduling_service.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorNameAndAppointmentDate(String doctorName, LocalDateTime appointmentDate);
}
