package com.fatec.estudamaisbackend.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienciaProfissionalDTO {
    private Long id;
    private String cargo;
    private String instituicao;
    private String periodo;
    private String descricao;
}