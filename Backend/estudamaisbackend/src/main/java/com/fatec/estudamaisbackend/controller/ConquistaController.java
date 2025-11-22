package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.Conquista;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.repository.ConquistaRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/**
 * CRUD para conquistas do professor.
 */
@RestController
@RequestMapping("/api/conquistas")
@CrossOrigin
public class ConquistaController {

    @Autowired
    private ConquistaRepository conquistaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ConquistaRequest req) {
        Optional<?> opt = usuarioRepository.findById(req.getProfessorId());
        if (opt.isEmpty() || !(opt.get() instanceof Professor)) return ResponseEntity.badRequest().body("Professor inválido");
        Professor p = (Professor) opt.get();

        Conquista c = new Conquista();
        c.setProfessor(p);
        c.setTituloConquista(req.getTituloConquista());
        c.setAno(req.getAno());
        c.setDescricao(req.getDescricao());

        Conquista salvo = conquistaRepository.save(c);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody ConquistaRequest req) {
        Optional<Conquista> opt = conquistaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Conquista c = opt.get();
        if (req.getTituloConquista() != null) c.setTituloConquista(req.getTituloConquista());
        if (req.getAno() != null) c.setAno(req.getAno());
        if (req.getDescricao() != null) c.setDescricao(req.getDescricao());
        conquistaRepository.save(c);
        return ResponseEntity.ok(c);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!conquistaRepository.existsById(id)) return ResponseEntity.notFound().build();
        conquistaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public static class ConquistaRequest {
        private Long professorId;
        private String tituloConquista;
        private Integer ano;
        private String descricao;

        public Long getProfessorId() { return professorId; }
        public void setProfessorId(Long professorId) { this.professorId = professorId; }
        public String getTituloConquista() { return tituloConquista; }
        public void setTituloConquista(String tituloConquista) { this.tituloConquista = tituloConquista; }
        public Integer getAno() { return ano; }
        public void setAno(Integer ano) { this.ano = ano; }
        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }
}