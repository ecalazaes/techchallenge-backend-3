package com.techchallenge.history_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    /**
     * Define a hierarquia de permissões e a ordem de execução dos filtros.
     * <p>
     * Configuração ajustada para:
     * <ul>
     * <li>Desabilitar CSRF (necessário para o funcionamento do GraphQL).</li>
     * <li>Permitir acesso público à interface visual GraphiQL para desenvolvimento.</li>
     * <li>Injetar a validação de Token antes da verificação padrão de usuário/senha.</li>
     * <li>Configurar a política de sessão como STATELESS.</li>
     * </ul>
     * </p>
     *
     * @param http Construtor da segurança HTTP.
     * @return A corrente de filtros configurada.
     * @throws Exception Caso ocorra erro na definição das políticas.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita proteção CSRF para aceitar mutações GraphQL externas
                .csrf(csrf -> csrf.disable())

                // Configura política de sessão como STATELESS (obrigatório para JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Liberação de infraestrutura e exploração da API
                        .requestMatchers("/graphiql", "/graphiql/**", "/graphql", "/graphql/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Adiciona o seu filtro de Token na cadeia de execução
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Permite a renderização do GraphiQL que utiliza iframes
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }
}
