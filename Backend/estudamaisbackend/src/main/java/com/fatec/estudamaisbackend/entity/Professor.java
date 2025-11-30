package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "professor")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Adicionado para facilitar
@DiscriminatorValue("PROFESSOR")
public class Professor extends Usuario {

    private String sobre;
    private String metodologia;
    private Double valorHora;
    private String fotoCertificado;
    private Boolean aprovado = false;
    private String linkPadraoAula;
    private Boolean usarLinkPadrao;

    // 🔥 EAGER fetch garante que materias sejam carregadas no GET /api/professores
    // 🔥 Sem cascade: matérias já existem no banco, só criamos o link na tabela professor_materia
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "professor_materia",
        joinColumns = @JoinColumn(name = "id_professor"),
        inverseJoinColumns = @JoinColumn(name = "id_materia")
    )
    private List<Materia> materias = new ArrayList<>();

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Disponibilidade> disponibilidades = new ArrayList<>();

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ExperienciaProfissional> experiencias = new ArrayList<>();

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Conquista> conquistas = new ArrayList<>();
}