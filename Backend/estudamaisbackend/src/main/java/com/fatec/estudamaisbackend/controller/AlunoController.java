package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.entity.Aula;
import com.fatec.estudamaisbackend.repository.AlunoRepository;
import com.fatec.estudamaisbackend.repository.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    @Autowired private AlunoRepository alunoRepository;
    @Autowired private AulaRepository aulaRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        return alunoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/aulas")
    public ResponseEntity<List<Aula>> getAulas(@PathVariable Long id) {
        List<Aula> aulas = aulaRepository.findByAlunoId(id);
        return ResponseEntity.ok(aulas);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Aluno alunoAtualizado) {
        Optional<Aluno> opt = alunoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        
        Aluno aluno = opt.get();
        
        // Atualiza campos de Usuario
        if (alunoAtualizado.getNomeCompleto() != null) 
            aluno.setNomeCompleto(alunoAtualizado.getNomeCompleto());
        if (alunoAtualizado.getTelefone() != null) 
            aluno.setTelefone(alunoAtualizado.getTelefone());
        if (alunoAtualizado.getFotoPerfil() != null) 
            aluno.setFotoPerfil(alunoAtualizado.getFotoPerfil());
        if (alunoAtualizado.getSexo() != null) 
            aluno.setSexo(alunoAtualizado.getSexo());
        if (alunoAtualizado.getDataNascimento() != null) 
            aluno.setDataNascimento(alunoAtualizado.getDataNascimento());
        
        // Atualiza campos específicos de Aluno (ONBOARDING)
        if (alunoAtualizado.getEscolaridade() != null) 
            aluno.setEscolaridade(alunoAtualizado.getEscolaridade());
        if (alunoAtualizado.getInteresse() != null) 
            aluno.setInteresse(alunoAtualizado.getInteresse());
        
        alunoRepository.save(aluno);
        return ResponseEntity.ok(aluno);
    }
}