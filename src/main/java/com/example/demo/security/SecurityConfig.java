package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Autenticação necessária."))
                        .accessDeniedHandler((request, response, exception) ->
                                response.sendError(HttpStatus.FORBIDDEN.value(), "Acesso negado.")))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error", "/public/register", "/public/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/imoveis", "/imoveis/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/imoveis/sabermais/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/imoveis/*/status").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/imoveis/*/status").authenticated()
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/imoveis/**", "/bairros/**", "/tipos_imoveis/**",
                                "/fotos/**", "/email/**").hasRole("ADMIN")
                        .anyRequest().denyAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
