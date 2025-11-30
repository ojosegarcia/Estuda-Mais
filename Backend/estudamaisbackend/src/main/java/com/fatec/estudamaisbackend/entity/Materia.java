package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "materia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia") // <--- CORREÇÃO
    private Long id;

    @Column(name = "nome_materia")
    private String nome;

    @Column(name = "descricao_materia")
    private String descricao;

    @Column(name = "icone_materia")
    private String icone;
}