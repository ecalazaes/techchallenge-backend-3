package com.techchallenge.scheduling_service.security;

import com.techchallenge.scheduling_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .password("{noop}" + user.getPassword()) // {noop} para ignorar BCrypt no teste
                        .authorities(user.getRole()) // Aqui ele pega ROLE_MEDICO, etc.
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado no banco!"));
    }
}
