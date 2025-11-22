package com.fatec.estudamaisbackend.dtos;

import lombok.*;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AlunoDTO extends UsuarioDTO {

    // frontend expects "escolaridade"
    @JsonProperty("escolaridade")
    private String escolaridade;

    @JsonProperty("interesse")
    private String interesse;

    @JsonProperty("aulas")
    private List<AulaResumoDTO> aulas;

    @JsonProperty("feedbacks")
    private List<FeedbackDTO> feedbacks;
}