package com.fatec.estudamaisbackend.controller;


import com.fatec.estudamaisbackend.entity.Materia;
import com.fatec.estudamaisbackend.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

/**
 * CRUD para matérias:
 * - GET /api/materias
 * - GET /api/materias/{id}
 * - POST /api/materias
 * - PUT /api/materias/{id}
 * - DELETE /api/materias/{id}
 */
@RestController
@RequestMapping("/api/materias")
@CrossOrigin
public class MateriaController {

    @Autowired
    private MateriaRepository materiaRepository;

    @GetMapping
    public ResponseEntity<List<Materia>> listar() {
        return ResponseEntity.ok(materiaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Materia> buscar(@PathVariable Long id) {
        Optional<Materia> opt = materiaRepository.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Materia> criar(@RequestBody Materia materia) {
        Materia salvo = materiaRepository.save(materia);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Materia> atualizar(@PathVariable Long id, @RequestBody Materia incoming) {
        return materiaRepository.findById(id).map(existing -> {
            existing.setNome(incoming.getNome());
            existing.setDescricao(incoming.getDescricao());
            existing.setIcone(incoming.getIcone());
            materiaRepository.save(existing);
            return ResponseEntity.ok(existing);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!materiaRepository.existsById(id)) return ResponseEntity.notFound().build();
        materiaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}