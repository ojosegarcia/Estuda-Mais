package com.fatec.estudamaisbackend.repository;

import com.fatec.estudamaisbackend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Modifying
    @Query("UPDATE Professor p SET p.aprovado = true WHERE p.aprovado = false OR p.aprovado IS NULL")
    int aprovarTodosProfessores();
}