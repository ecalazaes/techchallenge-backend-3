package com.techchallenge.scheduling_service.security;

import com.techchallenge.scheduling_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Serviço customizado para carregamento de dados do usuário durante o processo de autenticação.
 * <p>
 * Esta classe implementa a interface {@link UserDetailsService} do Spring Security,
 * servindo como ponte entre o banco de dados (via {@link UserRepository}) e o
 * motor de autenticação do framework.
 * </p>
 * <p>
 * <b>Segurança:</b> O método retorna as credenciais armazenadas em hash (BCrypt),
 * delegando a comparação de senhas ao {@code AuthenticationManager} configurado.
 * </p>
 *
 * @author Erick Calazães
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Recupera os detalhes do usuário a partir do seu nome de usuário (username).
     * <p>
     * O {@link UserDetails} retornado contém o username, a senha criptografada e as
     * permissões (authorities) necessárias para o controle de acesso baseado em roles (RBAC).
     * </p>
     *
     * @param username O nome de usuário a ser buscado na base de dados.
     * @return Uma instância de {@link UserDetails} preenchida com os dados da entidade.
     * @throws UsernameNotFoundException Caso o usuário não exista no repositório.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado no banco!"));
    }
}
