package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.dtos.ProfessorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        MateriaMapper.class,
        DisponibilidadeMapper.class,
        ExperienciaProfissionalMapper.class,
        ConquistaMapper.class,
        AulaResumoMapper.class
}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfessorMapper {

    @Mapping(source = "imagemCertificado", target = "fotoCertificado")
    ProfessorDTO toDto(Professor p);

    // reverse: ignore collections/relations, service must resolve materias by id etc.
    @Mapping(target = "materias", ignore = true)
    @Mapping(target = "disponibilidades", ignore = true)
    @Mapping(target = "experiencias", ignore = true)
    @Mapping(target = "conquistas", ignore = true)
    @Mapping(target = "aulas", ignore = true)
    @Mapping(target = "feedbacks", ignore = true)
    Professor toEntity(ProfessorDTO dto);

    List<ProfessorDTO> toDtoList(List<Professor> profs);
}