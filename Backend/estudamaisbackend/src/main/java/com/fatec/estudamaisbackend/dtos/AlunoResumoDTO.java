package com.fatec.estudamaisbackend.dtos;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlunoResumoDTO {
    private Long id;

    @JsonProperty("nomeCompleto")
    private String nomeCompleto;
}