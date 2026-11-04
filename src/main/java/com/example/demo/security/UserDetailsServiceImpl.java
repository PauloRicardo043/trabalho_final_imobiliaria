package com.example.demo.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuario = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
        String tipo = usuario.getTipo();
        if (!"ADMIN".equals(tipo) && !"USER".equals(tipo)) {
            throw new UsernameNotFoundException("Tipo de usuário inválido.");
        }
        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(tipo)
                .build();
    }
}
