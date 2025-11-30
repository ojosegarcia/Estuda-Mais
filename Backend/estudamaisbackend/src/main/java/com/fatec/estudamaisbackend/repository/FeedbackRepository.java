package com.fatec.estudamaisbackend.repository;

import com.fatec.estudamaisbackend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    // Busca pelo objeto Aula -> ID da aula
    // O Spring entende "AulaId" como "propriedade 'aula', sub-propriedade 'id'"
    Optional<Feedback> findByAulaId(Long aulaId);
    
    // Busca pelo campo idAluno (CORRIGIDO de findByAlunoId para findByIdAluno)
    List<Feedback> findByIdAluno(Long idAluno);
    
    // Busca pelo campo idProfessor (CORRIGIDO de findByProfessorId para findByIdProfessor)
    List<Feedback> findByIdProfessor(Long idProfessor);
}