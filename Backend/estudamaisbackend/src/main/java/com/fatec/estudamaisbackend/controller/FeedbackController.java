package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.Aula;
import com.fatec.estudamaisbackend.entity.Feedback;
import com.fatec.estudamaisbackend.repository.AulaRepository;
import com.fatec.estudamaisbackend.repository.FeedbackRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody FeedbackRequest req) {
        // 1. Validar se a Aula existe
        Optional<Aula> optAula = aulaRepository.findById(req.getIdAula());
        if (optAula.isEmpty()) {
            return ResponseEntity.badRequest().body("Aula inválida (ID não encontrado).");
        }
        Aula aula = optAula.get();

        // 2. Verificar se já existe feedback para esta aula (Regra 1:1)
        if (feedbackRepository.findByAulaId(aula.getId()).isPresent()) {
            return ResponseEntity.status(409).body("Já existe um feedback para esta aula.");
        }

        // 3. Validar IDs de usuários
        boolean alunoExiste = usuarioRepository.existsById(req.getIdAluno());
        boolean profExiste = usuarioRepository.existsById(req.getIdProfessor());

        if (!alunoExiste || !profExiste) {
            return ResponseEntity.badRequest().body("ID de Aluno ou Professor inválido.");
        }

        // 4. Criar e salvar
        Feedback feedback = new Feedback();
        feedback.setAula(aula);
        feedback.setIdAluno(req.getIdAluno());
        feedback.setIdProfessor(req.getIdProfessor());
        feedback.setNota(req.getNota());
        feedback.setComentarioPrivado(req.getComentarioPrivado());
        feedback.setComentarioPublico(req.getComentarioPublico());
        feedback.setRecomenda(req.getRecomenda());
        feedback.setDataFeedback(LocalDateTime.now());

        Feedback salvo = feedbackRepository.save(feedback);
        return ResponseEntity.status(201).body(salvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return feedbackRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/aula/{aulaId}")
    public ResponseEntity<?> porAula(@PathVariable Long aulaId) {
        return feedbackRepository.findByAulaId(aulaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DTO interno para simplificar
    public static class FeedbackRequest {
        private Long idAula;
        private Long idAluno;
        private Long idProfessor;
        private Integer nota;
        private String comentarioPrivado;
        private String comentarioPublico;
        private Boolean recomenda;

        public Long getIdAula() { return idAula; }
        public void setIdAula(Long idAula) { this.idAula = idAula; }
        public Long getIdAluno() { return idAluno; }
        public void setIdAluno(Long idAluno) { this.idAluno = idAluno; }
        public Long getIdProfessor() { return idProfessor; }
        public void setIdProfessor(Long idProfessor) { this.idProfessor = idProfessor; }
        public Integer getNota() { return nota; }
        public void setNota(Integer nota) { this.nota = nota; }
        public String getComentarioPrivado() { return comentarioPrivado; }
        public void setComentarioPrivado(String comentarioPrivado) { this.comentarioPrivado = comentarioPrivado; }
        public String getComentarioPublico() { return comentarioPublico; }
        public void setComentarioPublico(String comentarioPublico) { this.comentarioPublico = comentarioPublico; }
        public Boolean getRecomenda() { return recomenda; }
        public void setRecomenda(Boolean recomenda) { this.recomenda = recomenda; }
    }
}