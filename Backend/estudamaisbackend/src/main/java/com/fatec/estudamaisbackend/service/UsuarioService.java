package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.entity.Usuario;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> findAll() {
        return repository.findAll();
    }

    public Usuario findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    
    // Create e Update são feitos nos services específicos (Aluno/Professor) ou aqui se for genérico
    public Usuario save(Usuario u) {
        return repository.save(u);
    }
    
    public void delete(Long id) {
        repository.deleteById(id);
    }
}