package com.fatec.estudamaisbackend.dtos;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDTO {
    private Long id;

    @JsonProperty("idAula")
    private Long idAula;

    @JsonProperty("idAluno")
    private Long idAluno;

    @JsonProperty("idProfessor")
    private Long idProfessor;

    private Integer nota;

    @JsonProperty("comentarioPrivado")
    private String comentarioPrivado;

    @JsonProperty("comentarioPublico")
    private String comentarioPublico;

    @JsonProperty("dataFeedback")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime dataFeedback;

    private Boolean recomenda;
}