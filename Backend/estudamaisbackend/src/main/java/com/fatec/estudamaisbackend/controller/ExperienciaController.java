package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.ExperienciaProfissional;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.repository.ExperienciaProfissionalRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/professores/{professorId}/experiencias")
@CrossOrigin(origins = "*")
public class ExperienciaController {

    @Autowired
    private ExperienciaProfissionalRepository experienciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<?> listar(@PathVariable Long professorId) {
        List<ExperienciaProfissional> experiencias = experienciaRepository.findByProfessorIdOrderByIdDesc(professorId);
        return ResponseEntity.ok(experiencias);
    }

    @PostMapping
    public ResponseEntity<?> criar(@PathVariable Long professorId, @RequestBody ExperienciaRequest req) {
        Optional<?> opt = usuarioRepository.findById(professorId);
        
        if (opt.isEmpty() || !(opt.get() instanceof Professor)) {
            return ResponseEntity.badRequest().body("Professor inválido ou não encontrado.");
        }
        
        Professor p = (Professor) opt.get();

        ExperienciaProfissional e = new ExperienciaProfissional();
        e.setProfessor(p);
        e.setCargo(req.getCargo());
        e.setInstituicao(req.getInstituicao());
        e.setPeriodo(req.getPeriodo());
        e.setDescricao(req.getDescricao());

        ExperienciaProfissional salvo = experienciaRepository.save(e);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long professorId, @PathVariable Long id, @RequestBody ExperienciaRequest req) {
        Optional<ExperienciaProfissional> opt = experienciaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        
        ExperienciaProfissional e = opt.get();
        
        if (req.getCargo() != null) e.setCargo(req.getCargo());
        if (req.getInstituicao() != null) e.setInstituicao(req.getInstituicao());
        if (req.getPeriodo() != null) e.setPeriodo(req.getPeriodo());
        if (req.getDescricao() != null) e.setDescricao(req.getDescricao());
        
        experienciaRepository.save(e);
        return ResponseEntity.ok(e);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long professorId, @PathVariable Long id) {
        if (!experienciaRepository.existsById(id)) return ResponseEntity.notFound().build();
        experienciaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTO Interno
    public static class ExperienciaRequest {
        private String cargo;
        private String instituicao;
        private String periodo;
        private String descricao;

        public String getCargo() { return cargo; }
        public void setCargo(String cargo) { this.cargo = cargo; }
        public String getInstituicao() { return instituicao; }
        public void setInstituicao(String instituicao) { this.instituicao = instituicao; }
        public String getPeriodo() { return periodo; }
        public void setPeriodo(String periodo) { this.periodo = periodo; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }
}