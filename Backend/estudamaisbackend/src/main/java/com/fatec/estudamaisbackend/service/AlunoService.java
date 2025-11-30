package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlunoService {
    @Autowired
    private AlunoRepository repository;

    @Transactional
    public Aluno create(Aluno aluno) {
        if (aluno.getDataCadastro() == null)
            aluno.setDataCadastro(LocalDateTime.now());
        // 🔥 SEMPRE ATIVO: Força true para todos os alunos
        aluno.setAtivo(true);
        return repository.save(aluno);
    }
    
    public List<Aluno> findAll() { return repository.findAll(); }
    
    public Aluno findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }
    
    public Aluno update(Long id, Aluno alunoAtualizado) {
        Aluno alunoExistente = findById(id);
        
        // Atualiza campos de Usuario
        if (alunoAtualizado.getNomeCompleto() != null) 
            alunoExistente.setNomeCompleto(alunoAtualizado.getNomeCompleto());
        if (alunoAtualizado.getTelefone() != null) 
            alunoExistente.setTelefone(alunoAtualizado.getTelefone());
        if (alunoAtualizado.getFotoPerfil() != null) 
            alunoExistente.setFotoPerfil(alunoAtualizado.getFotoPerfil());
        if (alunoAtualizado.getSexo() != null) 
            alunoExistente.setSexo(alunoAtualizado.getSexo());
        if (alunoAtualizado.getDataNascimento() != null) 
            alunoExistente.setDataNascimento(alunoAtualizado.getDataNascimento());
        
        // Atualiza campos específicos de Aluno
        if (alunoAtualizado.getEscolaridade() != null) 
            alunoExistente.setEscolaridade(alunoAtualizado.getEscolaridade());
        if (alunoAtualizado.getInteresse() != null) 
            alunoExistente.setInteresse(alunoAtualizado.getInteresse());
        
        return repository.save(alunoExistente);
    }
    
    public void delete(Long id) { repository.deleteById(id); }
}