package com.fatec.estudamaisbackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateUsuarioDTO {
    @NotBlank
    private String nomeCompleto;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String tipoUsuario; // ALUNO ou PROFESSOR

    private String telefone;
    private LocalDate dataNascimento;
    private String sexo;       // Front chama de 'sexo' ou 'genero'? No seu JSON estava implícito. Vamos usar 'sexo' para bater com o banco.
    private String fotoPerfil; // Front: fotoPerfil

    // Campos Aluno
    private String escolaridade;
    private String interesse;

    // Campos Professor
    private String sobre;
    private String metodologia;
    private Double valorHora;
    private Boolean aprovado;
    private java.util.List<Long> materiaIds; // IDs das matérias que o professor ensina
}