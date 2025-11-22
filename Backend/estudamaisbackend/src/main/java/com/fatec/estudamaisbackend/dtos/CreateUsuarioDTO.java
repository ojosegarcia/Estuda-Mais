package com.fatec.estudamaisbackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para criação de Usuário. O campo tipoUsuario define qual subclasse será criada:
 * "ALUNO" ou "PROFESSOR".
 *
 * Contém campos comuns e os campos específicos (opcionais) para cada tipo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUsuarioDTO {

    @NotBlank
    private String tipoUsuario; // "ALUNO" ou "PROFESSOR"

    // campos comuns
    @NotBlank
    private String nomeCompleto;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String telefone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    private String genero; // corresponde a 'genero' na entidade

    private String imagemPerfil; // corresponde a imagemPerfil na entidade

    // campos específicos do Aluno (opcionais)
    private String educationLevel; // se sua entidade Aluno usa 'educationLevel'
    private String escolaridade;   // se a sua API/input usa 'escolaridade' (aceitamos ambos)
    private String interesse;

    // campos específicos do Professor (opcionais)
    private String sobre;
    private String metodologia;
    private Double valorHora;
    private Boolean aprovado;
}