package com.fatec.estudamaisbackend.dtos;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nomeCompleto;
    private String email;
    private String password;
    private String tipoUsuario; // "ALUNO" ou "PROFESSOR"
}