package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "class_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aula extends EntidadeBase {

    // nomes alinhados: professor, aluno, materia — assim o JSON fica parecido com o frontend (professor/aluno/materia opcionais)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Professor professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Materia materia;

    // dataAula -> LocalDate (frontend format "YYYY-MM-DD")
    @Column(name = "class_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("dataAula")
    private LocalDate dataAula;

    // horarioInicio / horarioFim -> LocalTime (format "HH:mm")
    @Column(name = "start_time", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @JsonProperty("horarioInicio")
    private LocalTime horarioInicio;

    @Column(name = "end_time", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @JsonProperty("horarioFim")
    private LocalTime horarioFim;

    // usar enum para status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @JsonProperty("statusAula")
    private StatusAula status;

    @Column(name = "meeting_link")
    @JsonProperty("linkReuniao")
    private String meetingLink;

    @Column(name = "class_value", nullable = false)
    @JsonProperty("valorAula")
    private Double classValue;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("dataCriacao")
    private LocalDateTime createdAt;

    // Conveniência: expor idProfessor/idAluno/idMateria para compatibilidade com frontend (eles usam ids primitivos)
    @JsonProperty("idProfessor")
    public Long getIdProfessor() {
        return professor != null ? professor.getId() : null;
    }

    @JsonProperty("idAluno")
    public Long getIdAluno() {
        return aluno != null ? aluno.getId() : null;
    }

    @JsonProperty("idMateria")
    public Long getIdMateria() {
        return materia != null ? materia.getId() : null;
    }

    @PrePersist
    protected void prePersistCreatedAt() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}