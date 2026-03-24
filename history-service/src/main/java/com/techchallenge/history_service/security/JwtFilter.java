package com.techchallenge.history_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro de interceptação JWT para autorização de requisições.
 * <p>
 * Esta classe estende {@link OncePerRequestFilter} para garantir uma única execução
 * por requisição. Sua responsabilidade é extrair o token Bearer do cabeçalho
 * {@code Authorization}, validá-lo via {@link JwtUtil} e estabelecer a autenticação
 * no contexto do Spring Security.
 * </p>
 * <p>
 * <b>Funcionamento:</b> Permite que o serviço de histórico seja Stateless,
 * confiando exclusivamente nas informações contidas no token (como Username e Role)
 * para aplicar as regras de controle de acesso.
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * Realiza a filtragem interna da requisição HTTP para extração e validação do token.
     * <p>
     * Caso um token válido seja identificado, o contexto de segurança é populado com
     * as autoridades (Roles) extraídas, permitindo o uso de {@code @PreAuthorize}
     * nos controladores GraphQL.
     * </p>
     *
     * @param request  A requisição HTTP recebida.
     * @param response A resposta HTTP associada.
     * @param filterChain A cadeia de filtros para prosseguimento do fluxo.
     * @throws ServletException Em caso de erro no processamento do servlet.
     * @throws IOException      Em caso de erro de entrada/saída.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, List.of(new SimpleGrantedAuthority(role)));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
