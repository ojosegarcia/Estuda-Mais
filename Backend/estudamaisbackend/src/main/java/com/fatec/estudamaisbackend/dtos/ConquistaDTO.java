package com.fatec.estudamaisbackend.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConquistaDTO {
    private Long id;
    private String tituloConquista;
    private Integer ano;
    private String descricao;
}