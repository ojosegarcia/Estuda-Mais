package com.fatec.estudamaisbackend.repository;


import com.fatec.estudamaisbackend.entity.ExperienciaProfissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienciaProfissionalRepository extends JpaRepository<ExperienciaProfissional, Long> {
    List<ExperienciaProfissional> findByProfessorId(Long professorId);
    long countByProfessorId(Long professorId);
    List<ExperienciaProfissional> findByProfessorIdOrderByIdDesc(Long professorId);
}