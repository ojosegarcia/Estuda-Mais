package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.ExperienciaProfissional;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.repository.ExperienciaProfissionalRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/**
 * CRUD básico para experiências profissionais.
 */
@RestController
@RequestMapping("/api/experiencias")
@CrossOrigin
public class ExperienciaController {

    @Autowired
    private ExperienciaProfissionalRepository experienciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ExperienciaRequest req) {
        Optional<?> opt = usuarioRepository.findById(req.getProfessorId());
        if (opt.isEmpty() || !(opt.get() instanceof Professor)) return ResponseEntity.badRequest().body("Professor inválido");
        Professor p = (Professor) opt.get();

        ExperienciaProfissional e = new ExperienciaProfissional();
        e.setProfessor(p);
        e.setPosition(req.getPosition());
        e.setInstitution(req.getInstitution());
        e.setPeriod(req.getPeriod());
        e.setDescription(req.getDescription());

        ExperienciaProfissional salvo = experienciaRepository.save(e);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ExperienciaRequest req) {
        Optional<ExperienciaProfissional> opt = experienciaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        ExperienciaProfissional e = opt.get();
        if (req.getPosition() != null) e.setPosition(req.getPosition());
        if (req.getInstitution() != null) e.setInstitution(req.getInstitution());
        if (req.getPeriod() != null) e.setPeriod(req.getPeriod());
        if (req.getDescription() != null) e.setDescription(req.getDescription());
        experienciaRepository.save(e);
        return ResponseEntity.ok(e);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!experienciaRepository.existsById(id)) return ResponseEntity.notFound().build();
        experienciaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public static class ExperienciaRequest {
        private Long professorId;
        private String position;
        private String institution;
        private String period;
        private String description;

        public Long getProfessorId() { return professorId; }
        public void setProfessorId(Long professorId) { this.professorId = professorId; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getInstitution() { return institution; }
        public void setInstitution(String institution) { this.institution = institution; }
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}