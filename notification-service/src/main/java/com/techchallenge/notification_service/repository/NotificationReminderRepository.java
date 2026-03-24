package com.techchallenge.notification_service.repository;

import com.techchallenge.notification_service.model.NotificationReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationReminderRepository extends JpaRepository<NotificationReminder, Long> {

    // Busca lembretes pendentes que acontecem entre Agora e as próximas 24 horas
    List<NotificationReminder> findByStatusAndAppointmentDateBefore(String status, LocalDateTime limit);

    void deleteByPatientEmailAndAppointmentDate(String email, LocalDateTime date);

    Optional<NotificationReminder> findByPatientEmailAndAppointmentDate(String email, LocalDateTime date);
}
