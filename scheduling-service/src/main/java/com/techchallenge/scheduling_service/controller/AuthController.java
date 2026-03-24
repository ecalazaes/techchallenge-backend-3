package com.techchallenge.scheduling_service.controller;

import com.techchallenge.scheduling_service.security.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        // 1. Carrega o usuário completo do banco/memória usando o username
        // Você precisa injetar o UserDetailsService no topo da classe (private final UserDetailsService userDetailsService;)
        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());

        // 2. Valida a senha
        if (user.getPassword().replace("{noop}", "").equals(request.getPassword())) {
            // 3. AGORA SIM: Passa o objeto 'user' (UserDetails) e não apenas a String
            return jwtUtil.generateToken(user);
        }

        throw new RuntimeException("Senha incorreta");
    }
}

// DTO simples para o login
@Data
class LoginRequest {
    private String username;
    private String password;
}

