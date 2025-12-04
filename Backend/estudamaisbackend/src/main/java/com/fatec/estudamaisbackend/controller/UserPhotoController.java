package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UserPhotoController {

    private final UsuarioRepository usuarioRepository;
    private final Path uploadDir = Paths.get("uploads");

    public UserPhotoController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        try { Files.createDirectories(uploadDir); } catch (IOException e) { /* ignore */ }
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        var userOpt = usuarioRepository.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        try {
            var filename = StringUtils.cleanPath(file.getOriginalFilename());
            var savedName = System.currentTimeMillis() + "_" + filename;
            Path target = uploadDir.resolve(savedName);
            Files.copy(file.getInputStream(), target);
            var user = userOpt.get();
            // gravamos caminho relativo para o frontend consumir: /uploads/<nome>
            user.setFotoPerfil("/uploads/" + savedName);
            usuarioRepository.save(user);
            return ResponseEntity.ok(Map.of("fotoPerfil", user.getFotoPerfil()));
        } catch (IOException ex) {
            return ResponseEntity.status(500).body(Map.of("error", "Não foi possível salvar a foto"));
        }
    }
}