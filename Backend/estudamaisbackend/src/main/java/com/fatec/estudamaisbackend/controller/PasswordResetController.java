package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.PasswordResetToken;
import com.fatec.estudamaisbackend.repository.PasswordResetTokenRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetTokenRepository prtRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetController(PasswordResetTokenRepository prtRepository,
                                   UsuarioRepository usuarioRepository,
                                   PasswordEncoder passwordEncoder) {
        this.prtRepository = prtRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        var usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            // por segurança não revelar; retorna 200
            return ResponseEntity.ok(Map.of("message", "Se o e-mail existir, será enviado um token."));
        }
        var user = usuarioOpt.get();
        String token = UUID.randomUUID().toString();
        var prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUsuarioId(user.getId());
        prt.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));
        prtRepository.save(prt);

        // Em ambiente de desenvolvimento retornamos o token no body para testar
        return ResponseEntity.ok(Map.of("message", "Token gerado (dev)", "token", token));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String novaSenha = body.get("novaSenha");
        Optional<PasswordResetToken> prtOpt = prtRepository.findByTokenAndUsedFalse(token);
        if (prtOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token inválido ou usado"));
        }
        var prt = prtOpt.get();
        if (prt.getExpiresAt().isBefore(Instant.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token expirado"));
        }
        var userOpt = usuarioRepository.findById(prt.getUsuarioId());
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Usuário não encontrado"));

        var user = userOpt.get();
        user.setPassword(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(user);

        prt.setUsed(true);
        prtRepository.save(prt);
        return ResponseEntity.ok(Map.of("message", "Senha atualizada com sucesso"));
    }
}