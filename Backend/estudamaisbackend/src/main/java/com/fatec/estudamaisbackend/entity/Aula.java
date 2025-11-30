package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "aula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aula") // <--- CORREÇÃO IMPORTANTE
    private Long id;

    // Relacionamentos
    @ManyToOne
    @JoinColumn(name = "id_professor")
    @JsonIgnoreProperties({"aulas", "materias", "disponibilidades", "experiencias", "conquistas"}) 
    private Professor professor;

    @ManyToOne
    @JoinColumn(name = "id_aluno")
    @JsonIgnoreProperties("aulas")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "id_materia")
    private Materia materia;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "data_aula")
    private LocalDate dataAula;

    @JsonFormat(pattern = "HH:mm")
    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @JsonFormat(pattern = "HH:mm")
    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_aula")
    private StatusAula statusAula;

    @Column(name = "link_reuniao")
    private String linkReuniao;
    
    @Column(name = "valor_aula")
    private Double valorAula;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "removido_pelo_aluno")
    private Boolean removidoPeloAluno = false;
    
    @Column(name = "removido_pelo_professor")
    private Boolean removidoPeloProfessor = false;

    // Getters virtuais para IDs
    @JsonProperty("idProfessor")
    public Long getIdProfessor() { return professor != null ? professor.getId() : null; }

    @JsonProperty("idAluno")
    public Long getIdAluno() { return aluno != null ? aluno.getId() : null; }

    @JsonProperty("idMateria")
    public Long getIdMateria() { return materia != null ? materia.getId() : null; }
    
    @PrePersist
    public void prePersist() {
        if(dataCriacao == null) dataCriacao = LocalDateTime.now();
    }
}