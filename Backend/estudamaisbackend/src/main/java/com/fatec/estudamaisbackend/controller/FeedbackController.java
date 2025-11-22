package com.fatec.estudamaisbackend.controller;


import com.fatec.estudamaisbackend.entity.*;
import com.fatec.estudamaisbackend.repository.FeedbackRepository;
import com.fatec.estudamaisbackend.repository.AulaRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Endpoints para feedbacks:
 * - POST /api/feedbacks
 * - GET  /api/feedbacks/{id}
 * - GET  /api/feedbacks/aula/{aulaId}
 */
@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody FeedbackRequest req) {
        Optional<Aula> optAula = aulaRepository.findById(req.getIdAula());
        if (optAula.isEmpty()) return ResponseEntity.badRequest().body("Aula inválida");

        Aula aula = optAula.get();
        if (feedbackRepository.findByAulaId(aula.getId()).isPresent()) {
            return ResponseEntity.status(409).body("Feedback já existe para esta aula");
        }

        Optional<Usuario> optAluno = usuarioRepository.findById(req.getIdAluno());
        Optional<Usuario> optProfessor = usuarioRepository.findById(req.getIdProfessor());
        if (optAluno.isEmpty() || optProfessor.isEmpty()) return ResponseEntity.badRequest().body("Aluno/professor inválidos");

        Feedback feedback = new Feedback();
        feedback.setAula(aula);
        feedback.setAluno((Aluno) optAluno.get());
        feedback.setProfessor((Professor) optProfessor.get());
        feedback.setNota(req.getNota());
        feedback.setComentarioPrivado(req.getComentarioPrivado());
        feedback.setComentarioPublico(req.getComentarioPublico());
        feedback.setDataFeedback(LocalDateTime.now());
        feedback.setRecomenda(req.getRecomenda());

        Feedback salvo = feedbackRepository.save(feedback);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        Optional<Feedback> opt = feedbackRepository.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/aula/{aulaId}")
    public ResponseEntity<?> porAula(@PathVariable Long aulaId) {
        Optional<Feedback> opt = feedbackRepository.findByAulaId(aulaId);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public static class FeedbackRequest {
        private Long idAula;
        private Long idAluno;
        private Long idProfessor;
        private Integer nota;
        private String comentarioPrivado;
        private String comentarioPublico;
        private Boolean recomenda = true;

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