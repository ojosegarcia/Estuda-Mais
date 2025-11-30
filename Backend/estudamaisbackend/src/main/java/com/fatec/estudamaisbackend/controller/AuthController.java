package com.fatec.estudamaisbackend.controller;
import com.fatec.estudamaisbackend.dtos.LoginRequest;
import com.fatec.estudamaisbackend.dtos.RegisterRequest;
import com.fatec.estudamaisbackend.entity.Usuario;
import com.fatec.estudamaisbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Usuario usuario = authService.login(request);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            Usuario usuario = authService.register(request);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            // Retorna 409 Conflict se já existir email
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}