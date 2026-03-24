package com.techchallenge.scheduling_service.service;

import com.techchallenge.scheduling_service.messaging.NotificationProducer;
import com.techchallenge.scheduling_service.model.Appointment;
import com.techchallenge.scheduling_service.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final NotificationProducer producer;

    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        // IMPORTANTE: Remova a linha que fazia o setPatientUsername(auth.getName())
        // O 'appointment' que chega aqui já terá o patientUsername preenchido pelo Jackson (JSON)
        if (repository.existsByDoctorNameAndAppointmentDate(appointment.getDoctorName(), appointment.getAppointmentDate())) {
            throw new RuntimeException("Médico já possui agendamento neste horário!");
        }
        appointment.setStatus("SCHEDULED");
        Appointment savedAppointment = repository.save(appointment);

        producer.sendConsultationEvent(savedAppointment);
        return savedAppointment;
    }

    @Transactional
    public Appointment updateAppointment(Long id, Appointment details) {
        // 1. Busca o existente
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado com ID: " + id));

        // 2. Atualiza os campos (pode vir do JSON do Postman)
        appointment.setAppointmentDate(details.getAppointmentDate());
        appointment.setDoctorName(details.getDoctorName());
        appointment.setStatus("UPDATED_SCHEDULED"); // Marcamos como atualizado para o Notification saber

        Appointment saved = repository.save(appointment);

        // 3. REQUISITO: Avisa o History e o Notification
        producer.sendConsultationEvent(saved);

        return saved;
    }

    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID não encontrado"));

        // Em vez de apagar do banco, mudamos o status para manter o histórico
        appointment.setStatus("CANCELLED");
        repository.save(appointment);

        // Avisa que foi cancelado
        producer.sendConsultationEvent(appointment);
    }
}
