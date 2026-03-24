package com.techchallenge.notification_service.repository;

import com.techchallenge.notification_service.model.NotificationReminder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest // Configura um banco de dados em memória
@ActiveProfiles("test") // Usa o arquivo application-test.properties
public class NotificationReminderRepositoryTest {

    @Autowired
    private NotificationReminderRepository repository;

    @Test
    void deveEncontrarLembretesPendentesParaAmanha() {
        // Criar um dado que deve ser encontrado (amanhã)
        NotificationReminder reminder = new NotificationReminder(null, "erick@exemplo.com", "Erick", "Dr. House", LocalDateTime.now().plusHours(12), "PENDENTE");
        repository.save(reminder);

        // Criar um dado que NÃO deve ser encontrado (daqui a 5 dias)
        NotificationReminder farAway = new NotificationReminder(null, "erick@exemplo.com", "Erick", "Dr. House", LocalDateTime.now().plusDays(5), "PENDENTE");
        repository.save(farAway);

        LocalDateTime limite = LocalDateTime.now().plusDays(1);
        List<NotificationReminder> pendentes = repository.findByStatusAndAppointmentDateBefore("PENDENTE", limite);

        assertThat(pendentes).hasSize(1);
        assertThat(pendentes.get(0).getPatientEmail()).isEqualTo("erick@exemplo.com");
    }
}
