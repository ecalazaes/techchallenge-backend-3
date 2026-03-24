package com.techchallenge.scheduling_service.model;

import jakarta.persistence.*;
import lombok.Data;


/**
 * Entidade que representa as contas de usuário e suas credenciais de acesso no sistema.
 * <p>
 * Gerencia a autenticação e a autorização baseada em perfis (roles) para garantir
 * que cada usuário acesse apenas as funcionalidades permitidas.
 * </p>
 * @author Erick Calazães
 *  */
@Entity
@Table(name = "tb_users")
@Data
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    /**
     * Define o nível de acesso e permissões do usuário no sistema.
     * <p>
     * Os valores aceitos são:
     * <ul>
     * <li><b>ROLE_MEDICO</b>: Acesso a agendas e prontuários.</li>
     * <li><b>ROLE_ENFERMEIRO</b>: Acesso a triagens e consultas básicas.</li>
     * <li><b>ROLE_PACIENTE</b>: Acesso a agendamentos próprios e histórico pessoal.</li>
     * </ul>
     * </p>
     */
    @Column(nullable = false)
    private String role;
}
