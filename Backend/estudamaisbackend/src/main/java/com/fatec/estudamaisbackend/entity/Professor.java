package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "professores")
@DiscriminatorValue("PROFESSOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Professor extends Usuario {

    @Column(name = "sobre")
    private String sobre;

    @Column(name = "metodologia")
    private String metodologia;

    @Column(name = "valor_hora")
    @JsonProperty("valorHora")
    private Double valorHora;

    @Column(name = "foto_certificado")
    @JsonProperty("fotoCertificado")
    private String imagemCertificado;

    @Column(name = "aprovado")
    private Boolean aprovado = false;
    // ManyToMany com Materia
    @ManyToMany
    @JoinTable(
            name = "professor_materias",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "materia_id")
    )
    @JsonProperty("materias")
    private List<Materia> materias = new ArrayList<>();

    // Disponibilidades do professor
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("disponibilidades")
    private List<Disponibilidade> disponibilidades = new ArrayList<>();

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("experiencias")
    private List<ExperienciaProfissional> experiencias = new ArrayList<>();

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("conquistas")
    private List<Conquista> conquistas = new ArrayList<>();

    // Aulas onde professor é responsável
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("aulas")
    private List<Aula> aulas = new ArrayList<>();

    // feedbacks recebidos pelo professor — nome no frontend é "feedbacks"
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("feedbacks")
    private List<Feedback> feedbacks = new ArrayList<>();
}