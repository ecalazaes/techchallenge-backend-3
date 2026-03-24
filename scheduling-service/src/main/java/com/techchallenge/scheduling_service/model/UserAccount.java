package com.techchallenge.scheduling_service.model;

import jakarta.persistence.*;
import lombok.Data;

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

    @Column(nullable = false)
    private String role; // Deve ser: ROLE_MEDICO, ROLE_ENFERMEIRO ou ROLE_PACIENTE
}
