package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Usuario extends EntidadeBase {

    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;

    @Column(name = "email", unique = true, nullable = false)
    private String email;


    @Column(name = "senha", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "data_nascimento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    // Frontend uses "sexo"
    @Column(name = "genero")
    @JsonProperty("sexo")
    private String genero;

    // Frontend uses "fotoPerfil"
    @Column(name = "imagem_perfil")
    @JsonProperty("fotoPerfil")
    private String imagemPerfil;


    @JsonProperty("dataCadastro")
    public java.time.LocalDateTime getDataCadastro() {
        return this.getCriadoEm();
    }

    @Column(name = "ativo")
    private Boolean ativo = true;

    // discriminator value provided by subclasses (ALUNO / PROFESSOR)
}