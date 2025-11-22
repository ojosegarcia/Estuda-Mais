package com.fatec.estudamaisbackend.dtos;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AulaDTO {
    private Long id;

    @JsonProperty("idProfessor")
    private Long idProfessor;

    @JsonProperty("idAluno")
    private Long idAluno;

    @JsonProperty("idMateria")
    private Long idMateria;

    // nested resumo optional
    private ProfessorResumoDTO professor;
    private AlunoResumoDTO aluno;
    private MateriaDTO materia;

    @JsonProperty("dataAula")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dataAula;

    @JsonProperty("horarioInicio")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime horarioInicio;

    @JsonProperty("horarioFim")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime horarioFim;

    @JsonProperty("statusAula")
    private String statusAula;

    @JsonProperty("linkReuniao")
    private String linkReuniao;

    @JsonProperty("valorAula")
    private Double valorAula;

    @JsonProperty("dataCriacao")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime dataCriacao;
}