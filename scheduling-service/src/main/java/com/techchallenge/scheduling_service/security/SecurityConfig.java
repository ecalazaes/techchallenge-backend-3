package com.techchallenge.scheduling_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração central de segurança da aplicação baseada em Spring Security 6.
 * <p>
 * Esta classe define a política de segurança HTTP, integrando o filtro JWT customizado
 * e estabelecendo as regras de autorização para os endpoints da API de agendamento.
 * </p>
 * <p>
 * <b>Principais Definições:</b>
 * <ul>
 * <li>Desabilita CSRF para permitir comunicação entre serviços via API REST.</li>
 * <li>Configura a política de sessão como <b>Stateless</b> (necessário para JWT).</li>
 * <li>Define o <b>BCrypt</b> como algoritmo de hash para senhas.</li>
 * <li>Gerencia as permissões de acesso baseadas em Authorities (RBAC).</li>
 * </ul>
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define o codificador de senhas da aplicação.
     * @return Uma instância de {@link BCryptPasswordEncoder} para hashing seguro.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expõe o {@link AuthenticationManager} como um Bean para ser utilizado no processo de login.
     * * @param config Configuração de autenticação do Spring.
     * @return O gerenciador de autenticação padrão.
     * @throws Exception Caso ocorra erro ao recuperar o manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura a cadeia de filtros de segurança (Security Filter Chain).
     * <p>
     * Define as rotas públicas (Login, Swagger) e as rotas protegidas que exigem
     * autoridades específicas (ROLE_MEDICO, ROLE_ENFERMEIRO).
     * </p>
     *
     * @param http Objeto para configuração de segurança HTTP.
     * @param jwtFilter Filtro customizado para validação de tokens JWT.
     * @return A configuração de segurança construída.
     * @throws Exception Em caso de erro na configuração dos filtros.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/appointments/**").hasAnyAuthority("ROLE_ENFERMEIRO", "ROLE_MEDICO")
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
