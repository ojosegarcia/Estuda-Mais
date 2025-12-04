package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.PasswordResetToken;
import com.fatec.estudamaisbackend.repository.PasswordResetTokenRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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

    private final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

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

    // Aceita JSON application/json
    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPasswordJson(@RequestBody Map<String, Object> body) {
        logger.debug("resetPasswordJson body: {}", body);
        String token = firstNonNullString(body.get("token"), body.get("t"));
        String senha = firstNonNullString(body.get("novaSenha"), body.get("rawPassword"), body.get("password"), body.get("newPassword"));
        return processReset(token, senha, body);
    }

    // Aceita form-urlencoded (application/x-www-form-urlencoded)
    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> resetPasswordForm(@RequestParam Map<String, String> params) {
        logger.debug("resetPasswordForm params: {}", params);
        String token = firstNonNullString(params.get("token"), params.get("t"));
        String senha = firstNonNullString(params.get("novaSenha"), params.get("rawPassword"), params.get("password"), params.get("newPassword"));
        return processReset(token, senha, params);
    }

    // Também aceita token via query param com JSON body (opcional)
    @PostMapping(value = "/reset-password", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> resetPasswordAny(@RequestParam(required = false) String token,
                                              @RequestBody(required = false) Map<String, Object> body) {
        logger.debug("resetPasswordAny token(param): {}, body: {}", token, body);
        String bodyToken = body != null ? firstNonNullString(body.get("token"), body.get("t")) : null;
        String finalToken = firstNonNullString(token, bodyToken);
        String senha = body != null ? firstNonNullString(body.get("novaSenha"), body.get("rawPassword"), body.get("password"), body.get("newPassword")) : null;
        return processReset(finalToken, senha, body != null ? body : Map.of("token", finalToken, "senha", senha));
    }

    private ResponseEntity<?> processReset(String token, String rawPassword, Object received) {
        logger.debug("processReset token: {}, received: {}", token, received);

        if (token == null || token.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            logger.warn("resetPassword called with missing token or password. received={}", received);
            return ResponseEntity.badRequest().body(Map.of("error", "token or password missing"));
        }

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
        try {
            user.setPassword(passwordEncoder.encode(rawPassword));
            usuarioRepository.save(user);

            prt.setUsed(true);
            prtRepository.save(prt);
            return ResponseEntity.ok(Map.of("message", "Senha atualizada com sucesso"));
        } catch (Exception ex) {
            logger.error("Erro ao resetar senha para token=" + token, ex);
            return ResponseEntity.status(500).body(Map.of("error", "Erro interno ao alterar a senha"));
        }
    }

    private String firstNonNullString(Object... objs) {
        for (Object o : objs) {
            if (o == null) continue;
            String s = String.valueOf(o);
            if (s != null && !s.isBlank()) return s;
        }
        return null;
    }
}