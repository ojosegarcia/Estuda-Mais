package com.fatec.estudamaisbackend.repository;


import com.fatec.estudamaisbackend.entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
    // busca por id do aluno/professor (Spring Data faz property traversal)
    List<Aula> findByAlunoId(Long alunoId);
    List<Aula> findByProfessorId(Long professorId);
}