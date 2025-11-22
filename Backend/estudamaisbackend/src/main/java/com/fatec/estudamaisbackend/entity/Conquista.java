package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conquistas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conquista extends EntidadeBase {

    @Column(name = "titulo_conquista", nullable = false)
    private String tituloConquista;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;
}