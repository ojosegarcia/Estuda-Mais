package com.fatec.estudamaisbackend.repository;


import com.fatec.estudamaisbackend.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    // retorna professores aprovados por matéria (útil para buscas)
    List<Professor> findByMateriasIdAndAprovadoTrue(Long materiaId);
    List<Professor> findByAprovadoTrue();
}