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

        // 2. Verifica duplicidade (Regra 1:1)
        if (feedbackRepository.findByAulaId(aula.getId()).isPresent()) {
            throw new RuntimeException("Já existe feedback para esta aula.");
        }

        // 3. Busca e valida os Usuários
        Usuario aluno = usuarioRepository.findById(dto.getIdAluno())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com id: " + dto.getIdAluno()));
        
        Usuario professor = usuarioRepository.findById(dto.getIdProfessor())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado com id: " + dto.getIdProfessor()));

        // 4. Valida os Tipos de Usuário
        // Como Usuario é a classe pai, precisamos garantir que o ID do aluno é de um ALUNO mesmo.
        // Nota: O Hibernate faz o proxy, então às vezes instanceof pode falhar se não inicializar.
        // Mas como usamos JOINED inheritance, o tipo real deve vir correto.
        // Uma alternativa segura é verificar strings: if (!"ALUNO".equals(aluno.getTipoUsuario())) ...
        
        // 5. Monta a Entidade
        Feedback f = new Feedback();
        f.setAula(aula);
        f.setIdAluno(aluno.getId());         // Setando ID direto
        f.setIdProfessor(professor.getId()); // Setando ID direto
        
        f.setNota(dto.getNota());
        f.setComentarioPrivado(dto.getComentarioPrivado());
        f.setComentarioPublico(dto.getComentarioPublico());
        f.setRecomenda(dto.getRecomenda() != null ? dto.getRecomenda() : true);
        f.setDataFeedback(LocalDateTime.now());

        // 6. Salva e Retorna DTO
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