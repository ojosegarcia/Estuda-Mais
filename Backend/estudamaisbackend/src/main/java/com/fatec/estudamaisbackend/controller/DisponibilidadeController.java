package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.Disponibilidade;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.repository.DisponibilidadeRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * CRUD de disponibilidades do professor.
 * - POST /api/disponibilidades
 * - GET  /api/disponibilidades/professor/{professorId}
 * - PUT  /api/disponibilidades/{id}
 * - DELETE /api/disponibilidades/{id}
 */
@RestController
@RequestMapping("/api/disponibilidades")
@CrossOrigin
public class DisponibilidadeController {

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody DisponibilidadeRequest req) {
        Optional<?> opt = usuarioRepository.findById(req.getProfessorId());
        if (opt.isEmpty() || !(opt.get() instanceof Professor)) return ResponseEntity.badRequest().body("Professor inválido");

        Professor prof = (Professor) opt.get();

        Disponibilidade d = new Disponibilidade();
        d.setProfessor(prof);
        d.setDayOfWeek(req.getDayOfWeek());
        d.setStartTime(LocalTime.parse(req.getStartTime()));
        d.setEndTime(LocalTime.parse(req.getEndTime()));
        d.setActive(req.getActive() == null ? true : req.getActive());

        Disponibilidade salvo = disponibilidadeRepository.save(d);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<Disponibilidade>> porProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(disponibilidadeRepository.findByProfessorId(professorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody DisponibilidadeRequest req) {
        Optional<Disponibilidade> opt = disponibilidadeRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Disponibilidade d = opt.get();
        if (req.getDayOfWeek() != null) d.setDayOfWeek(req.getDayOfWeek());
        if (req.getStartTime() != null) d.setStartTime(LocalTime.parse(req.getStartTime()));
        if (req.getEndTime() != null) d.setEndTime(LocalTime.parse(req.getEndTime()));
        if (req.getActive() != null) d.setActive(req.getActive());
        disponibilidadeRepository.save(d);
        return ResponseEntity.ok(d);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!disponibilidadeRepository.existsById(id)) return ResponseEntity.notFound().build();
        disponibilidadeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public static class DisponibilidadeRequest {
        private Long professorId;
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private Boolean active;

        public Long getProfessorId() { return professorId; }
        public void setProfessorId(Long professorId) { this.professorId = professorId; }
        public String getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }
}