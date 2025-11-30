package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.service.ProfessorService;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/professores", "/professores"})
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping
    public ResponseEntity<List<Professor>> findAll() {
        return ResponseEntity.ok(professorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professor> findById(@PathVariable Long id) {
        try {
            Professor p = professorService.findById(id);
            return ResponseEntity.ok(p);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Professor> create(@RequestBody Professor professor) {
        // any validation or default-setting can be in ProfessorService#create
        Professor saved = professorService.create(professor);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professor> update(@PathVariable Long id, @RequestBody Professor incoming) {
        try {
            Professor updated = professorService.update(id, incoming);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            professorService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<Professor> aprovar(@PathVariable Long id) {
        try {
            Professor professor = professorService.findById(id);
            professor.setAprovado(true);
            Professor updated = professorService.update(id, professor);
            System.out.println("✅ Professor ID=" + id + " aprovado com sucesso!");
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}