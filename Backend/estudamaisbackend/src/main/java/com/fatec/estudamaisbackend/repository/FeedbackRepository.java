package com.fatec.estudamaisbackend.repository;


import com.fatec.estudamaisbackend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByAulaId(Long aulaId);
    List<Feedback> findByProfessorId(Long professorId);
    List<Feedback> findByAlunoId(Long alunoId);
}