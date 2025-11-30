package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.dtos.ProfessorResumoDTO;
import com.fatec.estudamaisbackend.entity.Professor;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfessorResumoMapper {

    // O MapStruct faz o match automático pelos nomes:
    // nomeCompleto -> nomeCompleto
    // valorHora -> valorHora
    // fotoCertificado -> fotoCertificado (Agora que corrigimos a entidade, o nome bate!)
    
    ProfessorResumoDTO toDto(Professor p);
}