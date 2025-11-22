package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.dtos.FeedbackDTO;
import com.fatec.estudamaisbackend.entity.*;
import com.fatec.estudamaisbackend.mappers.FeedbackMapper;
import com.fatec.estudamaisbackend.repository.AulaRepository;
import com.fatec.estudamaisbackend.repository.FeedbackRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AulaRepository aulaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FeedbackMapper feedbackMapper;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           AulaRepository aulaRepository,
                           UsuarioRepository usuarioRepository,
                           FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.aulaRepository = aulaRepository;
        this.usuarioRepository = usuarioRepository;
        this.feedbackMapper = feedbackMapper;
    }

    public FeedbackDTO createFromDto(FeedbackDTO dto) {
        Aula aula = aulaRepository.findById(dto.getIdAula())
                .orElseThrow(() -> new ResourceNotFoundException("Aula não encontrada com id: " + dto.getIdAula()));

        if (feedbackRepository.findByAulaId(aula.getId()).isPresent()) {
            throw new IllegalArgumentException("Já existe feedback para esta aula");
        }

        Usuario aluno = usuarioRepository.findById(dto.getIdAluno())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + dto.getIdAluno()));
        Usuario professor = usuarioRepository.findById(dto.getIdProfessor())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com id: " + dto.getIdProfessor()));

        if (!(aluno instanceof Aluno) || !(professor instanceof Professor)) {
            throw new IllegalArgumentException("Tipos inválidos para aluno/professor");
        }

        Feedback f = new Feedback();
        f.setAula(aula);
        f.setAluno((Aluno) aluno);
        f.setProfessor((Professor) professor);
        f.setNota(dto.getNota());
        f.setComentarioPrivado(dto.getComentarioPrivado());
        f.setComentarioPublico(dto.getComentarioPublico());
        f.setDataFeedback(LocalDateTime.now());
        f.setRecomenda(dto.getRecomenda() == null ? true : dto.getRecomenda());

        Feedback salvo = feedbackRepository.save(f);
        return feedbackMapper.toDto(salvo);
    }

    public FeedbackDTO findById(Long id) {
        Feedback f = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback não encontrado com id: " + id));
        return feedbackMapper.toDto(f);
    }

    public void delete(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new ResourceNotFoundException("Feedback não encontrado com id: " + id);
        }
        feedbackRepository.deleteById(id);
    }
}