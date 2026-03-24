package com.techchallenge.scheduling_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilitário para geração, extração e validação de tokens JSON Web Token (JWT).
 * <p>
 * Esta classe centraliza a lógica de criptografia de tokens, permitindo que a aplicação
 * opere de forma Stateless. Ela utiliza o algoritmo <b>HS256</b> para assinatura digital,
 * garantindo que o payload não seja alterado após a emissão.
 * </p>
 * <p>
 * <b>Segurança:</b> A chave mestra deve ser compartilhada entre microserviços que
 * compõem o ecossistema para permitir a validação cruzada de tokens.
 * </p>
 *
 * @author Erick Calazães
 * @since 24/03/2026
 */
@Component
public class JwtUtil {

    /**
     * String base para a geração da chave de assinatura.
     * Deve conter pelo menos 32 caracteres para garantir a robustez do algoritmo HS256.
     */
    private final String SECRET_STRING = "SuaChaveSuperSecretaComMaisDe32CaracteresParaSeguranca";

    /**
     * Chave criptográfica gerada a partir da {@code SECRET_STRING}.
     */
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    /**
     * Gera um token JWT assinado para um usuário autenticado.
     * <p>
     * O token inclui o 'subject' (username) e uma claim personalizada de 'role' extraída
     * das permissões do usuário. O tempo de expiração padrão é de 24 horas.
     * </p>
     *
     * @param userDetails Objeto contendo as informações do usuário autenticado.
     * @return String contendo o token JWT no formato Compact Serialized.
     */
    public String generateToken(UserDetails userDetails) {

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 horas
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai o nome de usuário (Subject) de um token JWT.
     *
     * @param token O token JWT codificado.
     * @return O nome do usuário contido no token.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Valida a integridade e a expiração do token JWT.
     * <p>
     * O método verifica se a assinatura corresponde à chave secreta e se o tempo
     * de vida (expiração) do token ainda é válido.
     * </p>
     *
     * @param token O token JWT a ser validado.
     * @return {@code true} se o token for autêntico e válido; {@code false} caso contrário.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrai o perfil de acesso (Role) definido nas Claims do token.
     *
     * @param token O token JWT codificado.
     * @return A String representativa da role (ex: ROLE_MEDICO).
     */
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}
