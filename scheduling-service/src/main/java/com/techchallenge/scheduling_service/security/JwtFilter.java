package com.techchallenge.scheduling_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de segurança responsável por interceptar todas as requisições HTTP e validar o Token JWT.
 * <p>
 * Estende {@link OncePerRequestFilter} para garantir uma única execução por requisição.
 * O filtro extrai o token do cabeçalho 'Authorization', valida sua integridade via {@link JwtUtil}
 * e estabelece a autenticação no contexto do Spring Security.
 * </p>
 * <p>
 * <b>Fluxo:</b>
 * <ol>
 * <li>Verifica a presença do prefixo "Bearer " no Header.</li>
 * <li>Extrai o username e as roles do payload do token.</li>
 * <li>Valida o token contra a chave secreta e data de expiração.</li>
 * <li>Define o {@link SecurityContextHolder} caso a validação seja bem-sucedida.</li>
 * </ol>
 * </p>
 *
 * @author Erick Calazães
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Realiza a filtragem interna para processar a autenticação baseada em Token.
     *
     * @param request  A requisição HTTP recebida.
     * @param response A resposta HTTP a ser enviada.
     * @param filterChain A cadeia de filtros de segurança do Spring.
     * @throws ServletException Em caso de erro interno no processamento do Servlet.
     * @throws IOException      Em caso de erro de entrada/saída durante a filtragem.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtUtil.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            String role = jwtUtil.extractRole(jwt);

            System.out.println("Validando acesso para: " + username);
            System.out.println("Role detectada no Token: " + role);

            if (jwtUtil.validateToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
