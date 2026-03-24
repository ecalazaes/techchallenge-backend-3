package com.techchallenge.scheduling_service.controller;

import com.techchallenge.scheduling_service.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável pela autenticação de usuários e geração de tokens de acesso.
 * <p>
 * Este é o ponto de entrada para o sistema de segurança, permitindo que usuários
 * obtenham um token JWT válido para acessar endpoints protegidos.
 * </p>
 * @author Erick Calazães
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Realiza a autenticação do usuário e retorna um token JWT em caso de sucesso.
     * <p>
     * O método carrega os detalhes do usuário via {@link UserDetailsService},
     * valida a senha informada e, se correta, gera o token com as claims (roles) do usuário.
     * </p>
     * * @param request Objeto contendo as credenciais (username e password).
     * @return String contendo o Token JWT gerado.
     * @throws RuntimeException Caso as credenciais sejam inválidas.
     */
    @Operation(summary = "Realizar Login", description = "Valida as credenciais e retorna um Token JWT para ser usado no Header Authorization.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas ou senha incorreta")
    })
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());

        if (user.getPassword().replace("{noop}", "").equals(request.getPassword())) {
            return jwtUtil.generateToken(user);
        }

        throw new RuntimeException("Senha incorreta");
    }
}

/**
 * DTO (Data Transfer Object) para encapsular a requisição de login.
 */
@Data
class LoginRequest {
    private String username;
    private String password;
}

