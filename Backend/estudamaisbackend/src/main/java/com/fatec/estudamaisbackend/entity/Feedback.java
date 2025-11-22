package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "feedbacks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"aula_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback extends EntidadeBase {

    // 1 feedback por aula => unique constraint on aula_id enforced by table
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Aula aula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Professor professor;

    @Column(name = "nota", nullable = false)
    private Integer nota;

    @Column(name = "comentario_privado")
    @JsonProperty("comentarioPrivado")
    private String comentarioPrivado;

    @Column(name = "comentario_publico")
    @JsonProperty("comentarioPublico")
    private String comentarioPublico;

    @Column(name = "data_feedback")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("dataFeedback")
    private LocalDateTime dataFeedback;

    @Column(name = "recomenda")
    private Boolean recomenda = true;
}