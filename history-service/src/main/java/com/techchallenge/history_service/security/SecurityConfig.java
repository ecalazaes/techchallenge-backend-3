package com.techchallenge.history_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Extremamente necessário, ou o GraphQL vai dar erro 403 no POST
                .authorizeHttpRequests(auth -> auth
                        // Libera totalmente a interface visual e a rota da API para testes
                        .requestMatchers("/graphiql", "/graphiql/**", "/graphql", "/graphql/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                // ADICIONE ESTE BLOCO AQUI ABAIXO:
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()) // Permite carregar a interface no navegador
                );


        return http.build();
    }
}
