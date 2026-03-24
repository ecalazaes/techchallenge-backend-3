package com.techchallenge.scheduling_service.config;

import com.techchallenge.scheduling_service.model.UserAccount;
import com.techchallenge.scheduling_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Só insere se o banco estiver vazio
        if (userRepository.count() == 0) {

            System.out.println("🚀 Inicializando usuários no banco de dados...");

            UserAccount medico = new UserAccount();
            medico.setUsername("medico_erick");
            medico.setPassword("123");
            medico.setRole("ROLE_MEDICO");
            userRepository.save(medico);

            UserAccount enfermeiro = new UserAccount();
            enfermeiro.setUsername("enfermeiro_gabriel");
            enfermeiro.setPassword("123");
            enfermeiro.setRole("ROLE_ENFERMEIRO");
            userRepository.save(enfermeiro);

            UserAccount paciente = new UserAccount();
            paciente.setUsername("paciente_dani");
            paciente.setPassword("123");
            paciente.setRole("ROLE_PACIENTE");
            userRepository.save(paciente);

            UserAccount paciente2 = new UserAccount();
            paciente2.setUsername("paciente_gw2");
            paciente2.setPassword("123");
            paciente2.setRole("ROLE_PACIENTE");
            userRepository.save(paciente2);

            System.out.println("✅ Usuários criados com sucesso!");
        }
    }
}
