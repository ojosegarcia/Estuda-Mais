package com.fatec.estudamaisbackend.dtos;

import lombok.*;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class UsuarioDTO {
    private Long id;

    @JsonProperty("nomeCompleto")
    private String nomeCompleto;

    private String email;

    // password não é retornada no DTO de saída
    private String telefone;

    @JsonProperty("dataNascimento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    @JsonProperty("sexo")
    private String sexo;

    @JsonProperty("fotoPerfil")
    private String fotoPerfil;

    @JsonProperty("dataCadastro")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime dataCadastro;

    private Boolean ativo;

    @JsonProperty("tipoUsuario")
    private String tipoUsuario;
}