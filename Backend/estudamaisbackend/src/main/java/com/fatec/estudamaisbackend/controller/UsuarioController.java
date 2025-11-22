package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.dtos.CreateUsuarioDTO;
import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.entity.Usuario;
import com.fatec.estudamaisbackend.service.AlunoService;
import com.fatec.estudamaisbackend.service.ProfessorService;
import com.fatec.estudamaisbackend.service.UsuarioService;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller compatível com frontend legado (/usuarios) e com prefixo /api (/api/usuarios).
 * Recebe CreateUsuarioDTO em POST e cria Aluno ou Professor conforme tipoUsuario.
 */
@RestController
@RequestMapping({"/api/usuarios", "/usuarios"})
@CrossOrigin
public class UsuarioController {

    private final AlunoService alunoService;
    private final ProfessorService professorService;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(AlunoService alunoService,
                             ProfessorService professorService,
                             UsuarioService usuarioService,
                             PasswordEncoder passwordEncoder) {
        this.alunoService = alunoService;
        this.professorService = professorService;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos(@RequestParam(value = "tipoUsuario", required = false) String tipoUsuario) {
        List<Usuario> todos = usuarioService.findAll();
        if (tipoUsuario == null) return ResponseEntity.ok(todos);

        String tipoUpper = tipoUsuario.toUpperCase();
        List<Usuario> filtrados = todos.stream().filter(u -> {
            if ("ALUNO".equals(tipoUpper)) return u instanceof Aluno;
            if ("PROFESSOR".equals(tipoUpper)) return u instanceof Professor;
            return false;
        }).toList();

        return ResponseEntity.ok(filtrados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        try {
            Usuario u = usuarioService.findById(id);
            return ResponseEntity.ok(u);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cria Aluno ou Professor conforme CreateUsuarioDTO.tipoUsuario.
     * Valida com @Valid. Faz hash da senha antes de delegar ao service.
     */
    @PostMapping
    public ResponseEntity<?> criarUsuario(@Valid @RequestBody CreateUsuarioDTO dto) {
        String tipo = dto.getTipoUsuario().toUpperCase();

        // hash da senha
        String rawPwd = dto.getPassword();
        String hashed = rawPwd != null ? passwordEncoder.encode(rawPwd) : null;

        try {
            if ("ALUNO".equals(tipo)) {
                Aluno aluno = new Aluno();
                // campos comuns
                aluno.setNomeCompleto(dto.getNomeCompleto());
                aluno.setEmail(dto.getEmail());
                aluno.setPassword(hashed);
                aluno.setTelefone(dto.getTelefone());
                aluno.setDataNascimento(dto.getDataNascimento());
                aluno.setGenero(dto.getGenero());
                aluno.setImagemPerfil(dto.getImagemPerfil());
                aluno.setAtivo(true);

                // campos específicos: preferimos preencher com 'educationLevel' ou 'escolaridade'
                if (dto.getEducationLevel() != null) {
                    // se a entidade Aluno usa educationLevel:
                    try {
                        var m = aluno.getClass().getDeclaredMethod("setEducationLevel", String.class);
                        m.invoke(aluno, dto.getEducationLevel());
                    } catch (NoSuchMethodException ex) {
                        // tenta setar por 'escolaridade' (campo em português na entidade)
                        try {
                            var m2 = aluno.getClass().getDeclaredMethod("setEscolaridade", String.class);
                            m2.invoke(aluno, dto.getEducationLevel());
                        } catch (Exception ignored) {
                        }
                    }
                } else if (dto.getEscolaridade() != null) {
                    try {
                        var m = aluno.getClass().getDeclaredMethod("setEscolaridade", String.class);
                        m.invoke(aluno, dto.getEscolaridade());
                    } catch (Exception ignored) {
                    }
                }

                if (dto.getInteresse() != null) {
                    try {
                        var m = aluno.getClass().getDeclaredMethod("setInteresse", String.class);
                        m.invoke(aluno, dto.getInteresse());
                    } catch (Exception ignored) {
                    }
                }

                Aluno saved = alunoService.create(aluno);
                return ResponseEntity.status(201).body(saved);

            } else if ("PROFESSOR".equals(tipo)) {
                Professor professor = new Professor();

                // campos comuns
                professor.setNomeCompleto(dto.getNomeCompleto());
                professor.setEmail(dto.getEmail());
                professor.setPassword(hashed);
                professor.setTelefone(dto.getTelefone());
                professor.setDataNascimento(dto.getDataNascimento());
                professor.setGenero(dto.getGenero());
                professor.setImagemPerfil(dto.getImagemPerfil());
                professor.setAtivo(true);

                // campos específicos do professor
                professor.setSobre(dto.getSobre());
                professor.setMetodologia(dto.getMetodologia());
                if (dto.getValorHora() != null) professor.setValorHora(dto.getValorHora());
                if (dto.getAprovado() != null) professor.setAprovado(dto.getAprovado());

                Professor saved = professorService.create(professor);
                return ResponseEntity.status(201).body(saved);

            } else {
                return ResponseEntity.badRequest().body("tipoUsuario inválido. Use ALUNO ou PROFESSOR.");
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Falha ao criar usuário: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody CreateUsuarioDTO dto) {
        try {
            Usuario existing = usuarioService.findById(id);

            if (dto.getNomeCompleto() != null) existing.setNomeCompleto(dto.getNomeCompleto());
            if (dto.getTelefone() != null) existing.setTelefone(dto.getTelefone());
            if (dto.getGenero() != null) existing.setGenero(dto.getGenero());
            if (dto.getImagemPerfil() != null) existing.setImagemPerfil(dto.getImagemPerfil());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                existing.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            Usuario saved = usuarioService.update(id, existing);
            return ResponseEntity.ok(saved);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Falha ao atualizar usuário: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            usuarioService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}