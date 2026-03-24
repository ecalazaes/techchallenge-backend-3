package com.techchallenge.scheduling_service.config;

import com.techchallenge.scheduling_service.model.UserAccount;
import com.techchallenge.scheduling_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * Componente responsável pela carga inicial de dados (seeding) do sistema.
 * <p>
 * Implementa {@link CommandLineRunner} para realizar a inserção de usuários padrão
 * assim que o contexto da aplicação é carregado.
 * </p>
 * <p>
 * <b>Segurança:</b> As senhas são criptografadas utilizando o {@link PasswordEncoder}
 * definido na configuração de segurança, garantindo que nenhum dado sensível
 * seja armazenado em texto plano.
 * </p>
 * * @author Erick Calazães
 * @since 24/03/2026
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Executa a rotina de criação de usuários caso a base de dados esteja vazia.
     * * @param args Argumentos da linha de comando.
     * @throws Exception Caso ocorra erro na persistência dos dados.
     */
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {

            System.out.println("🚀 Inicializando usuários com criptografia BCrypt...");

            userRepository.save(createAccount("medico_erick", "123", "ROLE_MEDICO"));
            userRepository.save(createAccount("enfermeiro_gabriel", "123", "ROLE_ENFERMEIRO"));
            userRepository.save(createAccount("paciente_dani", "123", "ROLE_PACIENTE"));
            userRepository.save(createAccount("paciente_gw2", "123", "ROLE_PACIENTE"));

            System.out.println("✅ Usuários criados com sucesso!");
        }
    }

    /**
     * Método auxiliar para criação e codificação de contas de usuário.
     * * @param username Nome de usuário único.
     * @param rawPassword Senha em texto plano (será criptografada).
     * @param role Perfil de acesso do usuário.
     * @return Instância de UserAccount pronta para persistência.
     */
    private UserAccount createAccount(String username, String rawPassword, String role) {
        UserAccount account = new UserAccount();
        account.setUsername(username);
        // Criptografa a senha antes de salvar
        account.setPassword(passwordEncoder.encode(rawPassword));
        account.setRole(role);
        return account;
    }
}
