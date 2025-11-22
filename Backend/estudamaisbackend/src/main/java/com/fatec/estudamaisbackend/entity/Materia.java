package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "materias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Materia extends EntidadeBase {

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "icone")
    private String icone;

    // lista de professores (evitar serializar to prevent cycles)
    @ManyToMany(mappedBy = "materias")
    @JsonIgnore
    private List<Professor> professores = new ArrayList<>();
}