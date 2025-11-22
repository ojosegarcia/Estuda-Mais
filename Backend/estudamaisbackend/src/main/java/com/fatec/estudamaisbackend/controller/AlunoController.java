package com.fatec.estudamaisbackend.controller;


import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.entity.Aula;
import com.fatec.estudamaisbackend.entity.Feedback;
import com.fatec.estudamaisbackend.repository.AlunoRepository;
import com.fatec.estudamaisbackend.repository.AulaRepository;
import com.fatec.estudamaisbackend.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Endpoints para Aluno:
 * - GET /api/alunos
 * - GET /api/alunos/{id}
 * - PUT /api/alunos/{id}
 * - DELETE /api/alunos/{id}
 * - GET /api/alunos/{id}/aulas
 * - GET /api/alunos/{id}/feedbacks
 */
@RestController
@RequestMapping("/api/alunos")
@CrossOrigin
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping
    public ResponseEntity<List<Aluno>> listarTodos() {
        List<Aluno> alunos = alunoRepository.findAll();
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        Optional<Aluno> opt = alunoRepository.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        Optional<Aluno> opt = alunoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Aluno aluno = opt.get();
        if (payload.containsKey("nomeCompleto")) aluno.setNomeCompleto((String) payload.get("nomeCompleto"));
        if (payload.containsKey("telefone")) aluno.setTelefone((String) payload.get("telefone"));
        if (payload.containsKey("fotoPerfil")) aluno.setImagemPerfil((String) payload.get("fotoPerfil"));
        if (payload.containsKey("ativo")) aluno.setAtivo(Boolean.valueOf(payload.get("ativo").toString()));
        if (payload.containsKey("password")) {
            String pw = (String) payload.get("password");
            if (pw != null && !pw.isBlank()) aluno.setPassword(pw);
        }
        if (payload.containsKey("escolaridade")) aluno.setEducationLevel((String) payload.get("escolaridade"));
        if (payload.containsKey("interesse")) aluno.setInteresse((String) payload.get("interesse"));
        if (payload.containsKey("dataNascimento")) {
            Object d = payload.get("dataNascimento");
            if (d != null) {
                try {
                    LocalDate ld = LocalDate.parse(d.toString());
                    aluno.setDataNascimento(ld);
                } catch (DateTimeParseException ex) {
                    return ResponseEntity.badRequest().body(Map.of("error", "dataNascimento com formato inválido. Use yyyy-MM-dd"));
                }
            }
        }

        alunoRepository.save(aluno);
        return ResponseEntity.ok(aluno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!alunoRepository.existsById(id)) return ResponseEntity.notFound().build();
        alunoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/aulas")
    public ResponseEntity<List<Aula>> aulasDoAluno(@PathVariable Long id) {
        if (!alunoRepository.existsById(id)) return ResponseEntity.notFound().build();
        List<Aula> aulas = aulaRepository.findByAlunoId(id);
        return ResponseEntity.ok(aulas);
    }

    @GetMapping("/{id}/feedbacks")
    public ResponseEntity<List<Feedback>> feedbacksDoAluno(@PathVariable Long id) {
        if (!alunoRepository.existsById(id)) return ResponseEntity.notFound().build();
        List<Feedback> feedbacks = feedbackRepository.findByAlunoId(id);
        return ResponseEntity.ok(feedbacks);
    }
}