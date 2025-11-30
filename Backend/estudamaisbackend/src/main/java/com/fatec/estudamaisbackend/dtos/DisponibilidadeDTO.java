package com.fatec.estudamaisbackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fatec.estudamaisbackend.entity.DiaSemana;
import lombok.Data;

import java.time.LocalTime;

@Data
public class DisponibilidadeDTO {
    private Long id;
    
    private Long idProfessor; // Apenas o ID, não o objeto Professor
    
    private DiaSemana diaSemana; // O Enum é serializado como String (ex: "SEGUNDA")
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioInicio;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioFim;
    
    private Boolean ativo;
}