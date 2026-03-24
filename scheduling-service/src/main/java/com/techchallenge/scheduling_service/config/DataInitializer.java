package com.techchallenge.scheduling_service.config;

import com.techchallenge.scheduling_service.model.UserAccount;
import com.techchallenge.scheduling_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * Componente responsável pela carga inicial de dados (seeding) do sistema.
 * <p>
 * Implementa {@link CommandLineRunner} para executar a lógica de inserção de usuários
 * logo após a inicialização do contexto da aplicação Spring Boot.
 * </p>
 * <p>
 * <b>Nota:</b> Esta classe verifica se a base de dados de usuários está vazia antes de
 * realizar as inserções para evitar duplicidade em reinicializações do serviço.
 * </p>
 * @author Erick Calazães
 *
 */
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
