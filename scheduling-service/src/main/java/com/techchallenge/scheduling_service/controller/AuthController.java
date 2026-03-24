package com.techchallenge.scheduling_service.controller;

import com.techchallenge.scheduling_service.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsável pela autenticação de usuários e geração de tokens de acesso.
 * <p>
 * Atua como o ponto de entrada do sistema de segurança, delegando a validação de
 * credenciais para o AuthenticationManager e emitindo tokens JWT para acessos autorizados.
 * </p>
 * * @author Erick Calazães
 * @since 24/03/2026
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para gestão de acesso e segurança")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Realiza a autenticação do usuário e retorna um token JWT em caso de sucesso.
     * <p>
     * O processo utiliza o mecanismo padrão do Spring Security para validar o hash
     * da senha. Caso as credenciais sejam válidas, um token assinado é gerado.
     * </p>
     * * @param request Objeto contendo username e password.
     * @return String contendo o Token JWT (Bearer).
     */
    @Operation(summary = "Realizar Login", description = "Autentica o usuário via BCrypt e retorna um Token JWT válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário ou senha inválidos")
    })
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            UserDetails user = (UserDetails) auth.getPrincipal();
            return ResponseEntity.ok(jwtUtil.generateToken(user));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário ou senha inválidos");
        }
    }
}

/**
 * DTO (Data Transfer Object) para encapsular os dados de login.
 */
@Data
class LoginRequest {
    private String username;
    private String password;
}