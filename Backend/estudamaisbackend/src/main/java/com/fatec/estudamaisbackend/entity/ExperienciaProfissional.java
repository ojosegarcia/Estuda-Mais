package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "professional_experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExperienciaProfissional extends EntidadeBase {

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "institution", nullable = false)
    private String institution;

    @Column(name = "period", nullable = false)
    private String period;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Professor professor;
}