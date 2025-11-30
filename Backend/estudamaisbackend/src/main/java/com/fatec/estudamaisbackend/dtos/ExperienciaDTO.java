package com.fatec.estudamaisbackend.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ExperienciaDTO {
    
    private Long id;
    
    @NotBlank(message = "Cargo é obrigatório")
    @Size(min = 2, max = 255, message = "Cargo deve ter entre 2 e 255 caracteres")
    private String cargo;
    
    @NotBlank(message = "Instituição é obrigatória")
    @Size(min = 2, max = 255, message = "Instituição deve ter entre 2 e 255 caracteres")
    private String instituicao;
    
    @NotBlank(message = "Período é obrigatório")
    @Size(min = 3, max = 100, message = "Período deve ter entre 3 e 100 caracteres")
    @Pattern(regexp = "^.{3,100}$", message = "Ex: 'Jan/2020 - Dez/2023' ou '2020 - Atual'")
    private String periodo;
    
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;
}
