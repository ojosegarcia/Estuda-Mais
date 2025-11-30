package com.fatec.estudamaisbackend.controller;


import com.fatec.estudamaisbackend.dtos.CreateUsuarioDTO;
import com.fatec.estudamaisbackend.dtos.UpdateUsuarioDTO;
import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.entity.Usuario;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import com.fatec.estudamaisbackend.service.AlunoService;
import com.fatec.estudamaisbackend.service.ProfessorService;
import com.fatec.estudamaisbackend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final AlunoService alunoService;
    private final ProfessorService professorService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioController(AlunoService alunoService, ProfessorService professorService, 
                           UsuarioService usuarioService, UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.alunoService = alunoService;
        this.professorService = professorService;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarUsuario(@Valid @RequestBody CreateUsuarioDTO dto) {
        String tipo = dto.getTipoUsuario().toUpperCase();
        String encodedPwd = passwordEncoder.encode(dto.getPassword());

        try {
            if ("ALUNO".equals(tipo)) {
                Aluno aluno = new Aluno();
                aluno.setNomeCompleto(dto.getNomeCompleto());
                aluno.setEmail(dto.getEmail());
                aluno.setPassword(encodedPwd);
                aluno.setTelefone(dto.getTelefone());
                aluno.setDataNascimento(dto.getDataNascimento());
                aluno.setSexo(dto.getSexo());
                aluno.setFotoPerfil(dto.getFotoPerfil());
                aluno.setAtivo(true);
                aluno.setDataCadastro(LocalDateTime.now());
                aluno.setEscolaridade(dto.getEscolaridade());
                aluno.setInteresse(dto.getInteresse());

                return ResponseEntity.status(201).body(alunoService.create(aluno));

            } else if ("PROFESSOR".equals(tipo)) {
                Professor prof = new Professor();
                prof.setNomeCompleto(dto.getNomeCompleto());
                prof.setEmail(dto.getEmail());
                prof.setPassword(encodedPwd);
                prof.setTelefone(dto.getTelefone());
                prof.setDataNascimento(dto.getDataNascimento());
                prof.setSexo(dto.getSexo());
                prof.setFotoPerfil(dto.getFotoPerfil());
                prof.setAtivo(true);
                prof.setDataCadastro(LocalDateTime.now());
                prof.setSobre(dto.getSobre());
                prof.setMetodologia(dto.getMetodologia());
                prof.setValorHora(dto.getValorHora());
                
                // 🔥 SEMPRE APROVADO: Todos os professores são aprovados automaticamente
                prof.setAprovado(true);
                
                // 🔥 CRÍTICO: Processar materiaIds ao CRIAR professor
                System.out.println("📚 Criando professor com materiaIds: " + dto.getMateriaIds());
                Professor profCriado = professorService.createComMaterias(prof, dto.getMateriaIds());
                
                return ResponseEntity.status(201).body(profCriado);
            }
            return ResponseEntity.badRequest().body("Tipo de usuário inválido.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody UpdateUsuarioDTO dto) {
        try {
            // DEBUG: Verificando o que chega do Front
            System.out.println("=== ATUALIZANDO USUÁRIO " + id + " ===");
            if (dto.getMateriaIds() != null) {
                System.out.println("Matérias Recebidas (IDs): " + dto.getMateriaIds());
            } else {
                System.out.println("Matérias Recebidas: NULL");
            }

            Usuario usuarioExistente = usuarioService.findById(id);
            
            if (usuarioExistente instanceof Aluno) {
                Aluno aluno = (Aluno) usuarioExistente;
                if (dto.getNomeCompleto() != null) aluno.setNomeCompleto(dto.getNomeCompleto());
                if (dto.getTelefone() != null) aluno.setTelefone(dto.getTelefone());
                if (dto.getFotoPerfil() != null) aluno.setFotoPerfil(dto.getFotoPerfil());
                if (dto.getSexo() != null) aluno.setSexo(dto.getSexo());
                if (dto.getDataNascimento() != null) aluno.setDataNascimento(dto.getDataNascimento());
                if (dto.getEscolaridade() != null) aluno.setEscolaridade(dto.getEscolaridade());
                if (dto.getInteresse() != null) aluno.setInteresse(dto.getInteresse());
                
                return ResponseEntity.ok(alunoService.update(id, aluno)); // Use create ou update se existir
                
            } else if (usuarioExistente instanceof Professor) {
                Professor professor = (Professor) usuarioExistente;
                
                if (dto.getNomeCompleto() != null) professor.setNomeCompleto(dto.getNomeCompleto());
                if (dto.getTelefone() != null) professor.setTelefone(dto.getTelefone());
                if (dto.getFotoPerfil() != null) professor.setFotoPerfil(dto.getFotoPerfil());
                if (dto.getSexo() != null) professor.setSexo(dto.getSexo());
                if (dto.getDataNascimento() != null) professor.setDataNascimento(dto.getDataNascimento());
                if (dto.getSobre() != null) professor.setSobre(dto.getSobre());
                if (dto.getMetodologia() != null) professor.setMetodologia(dto.getMetodologia());
                if (dto.getValorHora() != null) professor.setValorHora(dto.getValorHora());
                if (dto.getFotoCertificado() != null) professor.setFotoCertificado(dto.getFotoCertificado());
                if (dto.getLinkPadraoAula() != null) professor.setLinkPadraoAula(dto.getLinkPadraoAula());
                if (dto.getUsarLinkPadrao() != null) professor.setUsarLinkPadrao(dto.getUsarLinkPadrao());
                
                // Chamada Correta do Service com IDs
                Professor result = professorService.updateComMaterias(id, professor, dto.getMateriaIds());
                
                return ResponseEntity.ok(result);
            }
            
            return ResponseEntity.badRequest().body("Tipo de usuário inválido.");
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao atualizar: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}