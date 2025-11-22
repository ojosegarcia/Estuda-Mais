package com.fatec.estudamaisbackend.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MateriaDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String icone;
}