package com.fatec.estudamaisbackend.repository;


import com.fatec.estudamaisbackend.entity.Conquista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConquistaRepository extends JpaRepository<Conquista, Long> {
    List<Conquista> findByProfessorId(Long professorId);
}