package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.dtos.ProfessorDTO;
import com.fatec.estudamaisbackend.entity.Professor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", uses = {
        // Se precisar de mappers auxiliares para Materia, etc, coloque aqui
}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfessorMapper {

    // REMOVEMOS AULAS E FEEDBACKS
    // Mantemos Materias, Disponibilidades, etc, se o DTO tiver e a Entidade tiver
    
    ProfessorDTO toDto(Professor p);

    @Mapping(target = "id", ignore = true)
    Professor toEntity(ProfessorDTO dto);

    List<ProfessorDTO> toDtoList(List<Professor> profs);
}