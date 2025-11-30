package com.fatec.estudamaisbackend.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConquistaDTO {
    private Long id;
    
    @NotBlank(message = "Título da conquista é obrigatório")
    @Size(min = 2, max = 255, message = "Título deve ter entre 2 e 255 caracteres")
    private String tituloConquista;
    
    @NotNull(message = "Ano é obrigatório")
    @Min(value = 1900, message = "Ano deve ser maior que 1900")
    @Max(value = 2100, message = "Ano deve ser menor que 2100")
    private Integer ano;
    
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;
}