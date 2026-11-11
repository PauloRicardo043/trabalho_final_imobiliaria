package com.example.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dtos.LoginDTO;
import com.example.demo.dtos.TokenDTO;
import com.example.demo.dtos.UserDTO;
import com.example.demo.security.JwtService;
import com.example.demo.services.UserService;

@RestController
@RequestMapping("/public")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserDTO user) {
        userService.registerPublic(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO login) {
        if (login == null || !StringUtils.hasText(login.getEmail()) || !StringUtils.hasText(login.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe e-mail e senha.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(login.getEmail(), login.getSenha()));

            UserDetails user = (UserDetails) authentication.getPrincipal();

            String perfil = user.getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Usuário sem permissão configurada."))
                    .getAuthority();

            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(new TokenDTO(token, "Bearer", perfil));

        } catch (BadCredentialsException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }
    }

}
