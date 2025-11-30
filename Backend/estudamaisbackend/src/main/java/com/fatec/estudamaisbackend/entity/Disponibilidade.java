package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "disponibilidade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Disponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidade") 
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_professor")
    @JsonIgnore 
    private Professor professor;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private DiaSemana diaSemana;

    @JsonFormat(pattern = "HH:mm")
    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @JsonFormat(pattern = "HH:mm")
    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    private Boolean ativo;
}