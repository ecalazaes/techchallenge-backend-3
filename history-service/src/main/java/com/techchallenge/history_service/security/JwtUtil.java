package com.techchallenge.history_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * Utilitário para decodificação e validação de tokens JWT no serviço de histórico.
 * <p>
 * Esta classe é responsável por interpretar os tokens emitidos pelo serviço de
 * agendamento, permitindo que o History Service identifique o usuário e suas
 * permissões (Roles) de forma descentralizada.
 * </p>
 * <p>
 * <b>Segurança:</b> Utiliza a mesma chave secreta compartilhada (Shared Secret)
 * entre os microserviços para garantir a integridade da assinatura digital.
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Component
public class JwtUtil {

    private final String SECRET_STRING = "SuaChaveSuperSecretaComMaisDe32CaracteresParaSeguranca";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    /**
     * Extrai o identificador do usuário (Subject/Username) do payload do token.
     *
     * @param token O token JWT fornecido no Header da requisição.
     * @return O nome de usuário contido no token.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Extrai a autoridade (Role) do usuário a partir das claims customizadas do token.
     *
     * @param token O token JWT fornecido.
     * @return A String representativa da Role (ex: ROLE_PACIENTE).
     */
    public String extractRole(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody().get("role", String.class);
    }

    /**
     * Valida se o token é autêntico, não foi adulterado e não está expirado.
     *
     * @param token O token JWT a ser verificado.
     * @return {@code true} se o token for válido; {@code false} em caso de erro ou expiração.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) { return false; }
    }
}
