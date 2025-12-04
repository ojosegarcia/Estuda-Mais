package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.dtos.FeedbackDTO;
import com.fatec.estudamaisbackend.entity.*;
import com.fatec.estudamaisbackend.mappers.FeedbackMapper;
import com.fatec.estudamaisbackend.repository.AulaRepository;
import com.fatec.estudamaisbackend.repository.FeedbackRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Transactional
    public FeedbackDTO create(FeedbackDTO dto) {
        // 1. Busca e valida a Aula
        Aula aula = aulaRepository.findById(dto.getIdAula())
                .orElseThrow(() -> new RuntimeException("Aula não encontrada com id: " + dto.getIdAula()));

        // 1.1 Verifica status REALIZADA
        if (aula.getStatusAula() != StatusAula.REALIZADA) {
            throw new RuntimeException("Feedback só pode ser enviado para aulas com status REALIZADA");
        }

        // 2. Verifica duplicidade (Regra 1:1)
        if (feedbackRepository.findByAulaId(aula.getId()).isPresent()) {
            throw new RuntimeException("Já existe feedback para esta aula.");
        }

        // 3. Busca e valida os Usuários (IDs enviados no DTO)
        // Garantir que o autor informado é participante da aula
        boolean autorEhAluno = dto.getIdAluno() != null && aula.getAluno() != null && dto.getIdAluno().equals(aula.getAluno().getId());
        boolean autorEhProfessor = dto.getIdProfessor() != null && aula.getProfessor() != null && dto.getIdProfessor().equals(aula.getProfessor().getId());

        if (!autorEhAluno && !autorEhProfessor) {
            throw new RuntimeException("Apenas participantes da aula (aluno ou professor) podem enviar feedback.");
        }

        // 4. Monta a Entidade
        Feedback f = new Feedback();
        f.setAula(aula);
        if (autorEhAluno) f.setIdAluno(dto.getIdAluno());
        if (autorEhProfessor) f.setIdProfessor(dto.getIdProfessor());

        f.setNota(dto.getNota());
        f.setComentarioPrivado(dto.getComentarioPrivado());
        f.setComentarioPublico(dto.getComentarioPublico());
        f.setRecomenda(dto.getRecomenda() != null ? dto.getRecomenda() : true);
        f.setDataFeedback(LocalDateTime.now());

        // 5. Salva e Retorna DTO
        Feedback salvo = feedbackRepository.save(f);
        return feedbackMapper.toDto(salvo);
    }

    public FeedbackDTO findById(Long id) {
        Feedback f = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback não encontrado com id: " + id));
        return feedbackMapper.toDto(f);
    }

    public void delete(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Feedback não encontrado com id: " + id);
        }
        feedbackRepository.deleteById(id);
    }
}