package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.dtos.ExperienciaDTO;
import com.fatec.estudamaisbackend.entity.ExperienciaProfissional;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.repository.ExperienciaProfissionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExperienciaService {

    private static final int MAX_EXPERIENCIAS = 5;

    @Autowired
    private ExperienciaProfissionalRepository repository;

    @Autowired
    private ProfessorService professorService;

    @Transactional
    public ExperienciaProfissional create(Long professorId, ExperienciaDTO dto) {
        Professor professor = professorService.findById(professorId);

        // Validação: máximo 5 experiências
        long count = repository.countByProfessorId(professorId);
        if (count >= MAX_EXPERIENCIAS) {
            throw new RuntimeException("Professor já possui o máximo de " + MAX_EXPERIENCIAS + " experiências");
        }

        ExperienciaProfissional exp = new ExperienciaProfissional();
        exp.setProfessor(professor);
        exp.setCargo(dto.getCargo());
        exp.setInstituicao(dto.getInstituicao());
        exp.setPeriodo(dto.getPeriodo());
        exp.setDescricao(dto.getDescricao());

        return repository.save(exp);
    }

    @Transactional
    public ExperienciaProfissional update(Long professorId, Long expId, ExperienciaDTO dto) {
        ExperienciaProfissional exp = repository.findById(expId)
                .orElseThrow(() -> new RuntimeException("Experiência não encontrada"));

        // Verifica se a experiência pertence ao professor
        if (!exp.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Experiência não pertence a este professor");
        }

        exp.setCargo(dto.getCargo());
        exp.setInstituicao(dto.getInstituicao());
        exp.setPeriodo(dto.getPeriodo());
        exp.setDescricao(dto.getDescricao());

        return repository.save(exp);
    }

    @Transactional
    public void delete(Long professorId, Long expId) {
        ExperienciaProfissional exp = repository.findById(expId)
                .orElseThrow(() -> new RuntimeException("Experiência não encontrada"));

        if (!exp.getProfessor().getId().equals(professorId)) {
            throw new RuntimeException("Experiência não pertence a este professor");
        }

        repository.delete(exp);
    }

    public List<ExperienciaProfissional> findByProfessor(Long professorId) {
        return repository.findByProfessorIdOrderByIdDesc(professorId);
    }
}
