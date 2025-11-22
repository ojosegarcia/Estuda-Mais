package com.fatec.estudamaisbackend.dtos;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessorResumoDTO {
    private Long id;

    @JsonProperty("nomeCompleto")
    private String nomeCompleto;

    @JsonProperty("valorHora")
    private Double valorHora;

    @JsonProperty("fotoCertificado")
    private String fotoCertificado;
}