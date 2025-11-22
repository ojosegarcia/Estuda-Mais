package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "students")
@DiscriminatorValue("ALUNO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluno extends Usuario {

    // frontend expects "escolaridade"
    @Column(name = "education_level")
    @JsonProperty("escolaridade")
    private String educationLevel;

    // frontend expects "interesse" as string
    @Column(name = "interesse")
    private String interesse;

    // relacionamento com aulas: mappedBy = "aluno"
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("aulas")
    private List<Aula> aulas = new ArrayList<>();

    // feedbacks deixados pelo aluno
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("feedbacks")
    private List<Feedback> feedbacks = new ArrayList<>();
}