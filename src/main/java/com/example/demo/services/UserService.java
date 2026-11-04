package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dtos.UserDTO;
import com.example.demo.models.UserModel;
import com.example.demo.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> lista = repository.findAll();
        return lista;        
    }

    public UserModel find(Integer id){
        Optional<UserModel> user = repository.findById(id);
        return user.orElse(null);
    }

    public UserModel insert(UserModel user){
        return repository.save(user);
    }

    public UserModel update(UserModel user){
        if (user == null || user.getId() == null) {
            return null;
        }
        UserModel usuarioExistente = find(user.getId());
        if (usuarioExistente == null) {
            return null;
        }

        if (user.getNome() != null) {
            usuarioExistente.setNome(user.getNome());
        }
        if (user.getEmail() != null) {
            usuarioExistente.setEmail(user.getEmail());
        }
        if (user.getSenha() != null) {
            usuarioExistente.setSenha(user.getSenha());
        }
        if (user.getTipo() != null) {
            usuarioExistente.setTipo(user.getTipo());
        }

        return repository.save(usuarioExistente);
    }

    public void delete(Integer id){
        repository.deleteById(id);
    }

    public UserModel insert(UserDTO dto) {
        if (dto == null || !StringUtils.hasText(dto.getNome())
                || !StringUtils.hasText(dto.getEmail()) || !StringUtils.hasText(dto.getSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe nome, e-mail e senha.");
        }
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }

        String tipo = StringUtils.hasText(dto.getTipo()) ? dto.getTipo().trim().toUpperCase() : "USER";
        if (!tipo.equals("ADMIN") && !tipo.equals("USER")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de usuário inválido.");
        }

        UserModel model = new UserModel();
        model.setNome(dto.getNome());
        model.setEmail(dto.getEmail());
        model.setSenha(passwordEncoder.encode(dto.getSenha()));
        model.setTipo(tipo);
        return repository.save(model);
    }

    public UserModel registerPublic(UserDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do usuário são obrigatórios.");
        }
        dto.setTipo("USER");
        return insert(dto);
    }

    public UserModel update(UserDTO dto) {
        UserModel model = find(dto.getId());
        if (model == null) {
            return null;
        }
        if (dto.getNome() != null) {
            model.setNome(dto.getNome());
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(model.getEmail())) {
            if (repository.findByEmail(dto.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
            }
            model.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getSenha())) {
            model.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        if (dto.getTipo() != null) {
            String tipo = dto.getTipo().trim().toUpperCase();
            if (!tipo.equals("ADMIN") && !tipo.equals("USER")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de usuário inválido.");
            }
            model.setTipo(tipo);
        }
        return repository.save(model);
    }
}
