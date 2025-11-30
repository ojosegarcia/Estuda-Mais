package com.fatec.estudamaisbackend.repository;

import com.fatec.estudamaisbackend.entity.Aula;
import com.fatec.estudamaisbackend.entity.StatusAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    
    // Query methods existentes
    List<Aula> findByAlunoId(Long alunoId);
    List<Aula> findByProfessorId(Long professorId);
    
    // Buscar aulas de um professor em uma data específica (para verificar slots ocupados)
    List<Aula> findByProfessorIdAndDataAula(Long professorId, LocalDate dataAula);
    
    // Buscar aulas de um aluno que ainda não foram removidas por ele
    List<Aula> findByAlunoIdAndRemovidoPeloAluno(Long alunoId, Boolean removidoPeloAluno);
    
    // Buscar aulas de um professor que ainda não foram removidas por ele
    List<Aula> findByProfessorIdAndRemovidoPeloProfessor(Long professorId, Boolean removidoPeloProfessor);
    
    // Buscar aulas ocupadas (CONFIRMADA ou SOLICITADA) em uma data/horário específico para validação de conflito
    @Query("SELECT a FROM Aula a WHERE a.professor.id = :professorId " +
           "AND a.dataAula = :dataAula " +
           "AND a.statusAula IN ('SOLICITADA', 'CONFIRMADA') " +
           "AND ((a.horarioInicio <= :horarioInicio AND a.horarioFim > :horarioInicio) " +
           "OR (a.horarioInicio < :horarioFim AND a.horarioFim >= :horarioFim) " +
           "OR (a.horarioInicio >= :horarioInicio AND a.horarioFim <= :horarioFim))")
    List<Aula> findConflitosDeHorario(
        @Param("professorId") Long professorId,
        @Param("dataAula") LocalDate dataAula,
        @Param("horarioInicio") java.time.LocalTime horarioInicio,
        @Param("horarioFim") java.time.LocalTime horarioFim
    );
}