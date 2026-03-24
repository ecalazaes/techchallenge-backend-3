package com.techchallenge.notification_service.service;

import com.techchallenge.notification_service.dto.AppointmentEventDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender; // Simula o motor de e-mail

    @InjectMocks
    private EmailService emailService; // Injeta o simulador no seu serviço

    @Test
    void deveEnviarEmailComSucesso() {
        // 1. GIVEN (Dado que temos um agendamento)
        AppointmentEventDTO dto = new AppointmentEventDTO();
        dto.setPatientEmail("teste@teste.com");
        dto.setPatientName("Erick");
        dto.setDoctorName("Dr. House");
        dto.setAppointmentDate(LocalDateTime.now().plusDays(1));
        dto.setStatus("SCHEDULED");

        // 2. WHEN (Quando chamamos o envio)
        emailService.sendAppointmentEmail(dto);

        // 3. THEN (Verifique se o mailSender foi chamado pelo menos uma vez)
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
