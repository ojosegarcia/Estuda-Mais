package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.entity.Usuario;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

/**
 * Endpoints mínimos de autenticação usados pelo frontend mock.
 * Observação: autenticação simples (plaintext). Em produção, use BCrypt + JWT.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email and password are required"));
        }

        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
        }

        Usuario user = opt.get();
        // Compare plaintext for mock. Replace with hash check in production.
        if (!password.equals(user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
        }

        // password is WRITE_ONLY in entity -> won't be serialized
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> payload) {
        Object tipoObj = payload.get("tipoUsuario");
        if (tipoObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "tipoUsuario é obrigatório"));
        }
        String tipo = tipoObj.toString();

        String email = (String) payload.get("email");
        String password = (String) payload.get("password");
        String nome = (String) payload.get("nomeCompleto");

        if (email == null || password == null || nome == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email, password e nomeCompleto são obrigatórios"));
        }

        if (usuarioRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "email já cadastrado"));
        }

        if ("ALUNO".equalsIgnoreCase(tipo)) {
            Aluno aluno = new Aluno();
            aluno.setNomeCompleto(nome);
            aluno.setEmail(email);
            aluno.setPassword(password);
            aluno.setAtivo(true);
            if (payload.get("escolaridade") != null) aluno.setEducationLevel((String) payload.get("escolaridade"));
            if (payload.get("interesse") != null) aluno.setInteresse((String) payload.get("interesse"));
            Usuario salvo = usuarioRepository.save(aluno);
            return ResponseEntity.ok(salvo);
        } else if ("PROFESSOR".equalsIgnoreCase(tipo)) {
            Professor prof = new Professor();
            prof.setNomeCompleto(nome);
            prof.setEmail(email);
            prof.setPassword(password);
            prof.setAtivo(true);
            Usuario salvo = usuarioRepository.save(prof);
            return ResponseEntity.ok(salvo);
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "tipoUsuario inválido: use ALUNO ou PROFESSOR"));
        }
    }
}