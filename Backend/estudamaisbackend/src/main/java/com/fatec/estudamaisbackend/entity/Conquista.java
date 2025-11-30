package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "conquista")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conquista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conquista") 
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_professor")
    @JsonIgnore
    private Professor professor;

    @Column(name = "titulo_conquista")
    private String tituloConquista;

    private Integer ano;
    private String descricao;
}