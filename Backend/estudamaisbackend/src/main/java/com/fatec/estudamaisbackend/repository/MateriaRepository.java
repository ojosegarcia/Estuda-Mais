package com.fatec.estudamaisbackend.repository;

import com.fatec.estudamaisbackend.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
}