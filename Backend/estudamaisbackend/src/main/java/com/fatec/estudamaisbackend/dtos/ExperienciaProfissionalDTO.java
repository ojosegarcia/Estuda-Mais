package com.fatec.estudamaisbackend.dtos;

import lombok.Data;

@Data
public class ExperienciaProfissionalDTO {
    private Long id;
    private Long idProfessor; // Útil para vincular
    private String cargo;
    private String instituicao;
    private String periodo;
    private String descricao;
}