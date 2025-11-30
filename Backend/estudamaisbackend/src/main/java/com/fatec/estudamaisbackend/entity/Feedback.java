package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_feedback") // <--- CORREÇÃO
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_aula")
    private Aula aula;

    @Column(name = "id_aluno_autor")
    private Long idAluno;

    @Column(name = "id_professor_alvo")
    private Long idProfessor;

    private Integer nota;
    
    @Column(name = "comentario_privado")
    private String comentarioPrivado;
    
    @Column(name = "comentario_publico")
    private String comentarioPublico;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "data_feedback")
    private LocalDateTime dataFeedback;
    
    private Boolean recomenda;
    
    public Long getIdAula() { return aula != null ? aula.getId() : null; }
}