package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.entity.Aula;
import com.fatec.estudamaisbackend.entity.Feedback;
import com.fatec.estudamaisbackend.repository.AlunoRepository;
import com.fatec.estudamaisbackend.repository.AulaRepository;
import com.fatec.estudamaisbackend.repository.FeedbackRepository;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final AulaRepository aulaRepository;
    private final FeedbackRepository feedbackRepository;

    public AlunoService(AlunoRepository alunoRepository,
                        AulaRepository aulaRepository,
                        FeedbackRepository feedbackRepository) {
        this.alunoRepository = alunoRepository;
        this.aulaRepository = aulaRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public List<Aluno> findAll() {
        return alunoRepository.findAll();
    }

    public Aluno findById(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + id));
    }

    public Aluno create(Aluno aluno) {
        return alunoRepository.save(aluno);
    }

    public Aluno update(Long id, Aluno update) {
        Aluno existing = findById(id);
        existing.setNomeCompleto(update.getNomeCompleto());
        existing.setTelefone(update.getTelefone());
        existing.setImagemPerfil(update.getImagemPerfil());
        existing.setAtivo(update.getAtivo());
        if (update.getPassword() != null && !update.getPassword().isBlank()) {
            existing.setPassword(update.getPassword());
        }
        existing.setEducationLevel(update.getEducationLevel());
        existing.setInteresse(update.getInteresse());
        existing.setDataNascimento(update.getDataNascimento());
        return alunoRepository.save(existing);
    }

    public void delete(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aluno não encontrado com id: " + id);
        }
        alunoRepository.deleteById(id);
    }

    public List<Aula> findAulasByAlunoId(Long alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new ResourceNotFoundException("Aluno não encontrado com id: " + alunoId);
        }
        return aulaRepository.findByAlunoId(alunoId);
    }

    public List<Feedback> findFeedbacksByAlunoId(Long alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new ResourceNotFoundException("Aluno não encontrado com id: " + alunoId);
        }
        return feedbackRepository.findByAlunoId(alunoId);
    }
}