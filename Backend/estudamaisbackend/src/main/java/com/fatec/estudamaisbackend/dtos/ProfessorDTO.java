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
public class ProfessorDTO extends UsuarioDTO {

    private String sobre;
    private String metodologia;

    @JsonProperty("valorHora")
    private Double valorHora;

    @JsonProperty("fotoCertificado")
    private String fotoCertificado;

    private Boolean aprovado;

    @JsonProperty("materias")
    private List<MateriaDTO> materias;

    @JsonProperty("disponibilidades")
    private List<DisponibilidadeDTO> disponibilidades;

    @JsonProperty("experiencias")
    private List<ExperienciaProfissionalDTO> experiencias;

    @JsonProperty("conquistas")
    private List<ConquistaDTO> conquistas;

    @JsonProperty("feedbacks")
    private List<FeedbackDTO> feedbacks;

    @JsonProperty("aulas")
    private List<AulaResumoDTO> aulas;
}