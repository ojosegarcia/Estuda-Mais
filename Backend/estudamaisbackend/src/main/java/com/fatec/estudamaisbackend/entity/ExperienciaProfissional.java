package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "experiencia_profissional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExperienciaProfissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_experiencia")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_professor", nullable = false) // Corrigido de teacher_id para id_professor
    @JsonIgnore // Evita loop infinito ao serializar o Professor
    private Professor professor;

    @Column(name = "cargo", nullable = false) // Corrigido de position para cargo
    private String cargo;

    @Column(name = "empresa_instituicao", nullable = false) // Corrigido de institution para empresa_instituicao
    private String instituicao;

    @Column(name = "periodo", nullable = false) // Mantido, mas garantindo mapeamento
    private String periodo;

    @Column(name = "descricao") // Corrigido de description para descricao
    private String descricao;
}