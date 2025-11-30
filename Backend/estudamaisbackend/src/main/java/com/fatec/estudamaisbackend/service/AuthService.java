package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.dtos.LoginRequest;
import com.fatec.estudamaisbackend.dtos.RegisterRequest;
import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.entity.Usuario;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario login(LoginRequest request) {
        // 1. Busca o usuário pelo email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Verifica a senha (Hash do banco vs Texto puro do front)
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        return usuario;
    }

    public Usuario register(RegisterRequest request) {
        // 1. Verifica se já existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario novoUsuario;

        // 2. Instancia a classe filha correta (Polimorfismo)
        if ("PROFESSOR".equalsIgnoreCase(request.getTipoUsuario())) {
            Professor prof = new Professor();
            prof.setAprovado(false); // Professor começa não aprovado?
            prof.setUsarLinkPadrao(false);
            novoUsuario = prof;
        } else {
            // Default é ALUNO
            Aluno aluno = new Aluno();
            aluno.setInteresse("APRENDER_NOVO"); // Valor default
            novoUsuario = aluno;
        }

        // 3. Preenche os dados comuns
        novoUsuario.setNomeCompleto(request.getNomeCompleto());
        novoUsuario.setEmail(request.getEmail());
        // CRIPTOGRAFA A SENHA ANTES DE SALVAR!
        novoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        novoUsuario.setAtivo(true);
        novoUsuario.setDataCadastro(LocalDateTime.now());
        
        // O tipo_usuario é preenchido automaticamente pelo Hibernate por causa da herança

        return usuarioRepository.save(novoUsuario);
    }
}