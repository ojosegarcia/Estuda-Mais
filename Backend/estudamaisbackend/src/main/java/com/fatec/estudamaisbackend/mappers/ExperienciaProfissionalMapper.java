package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.dtos.ExperienciaProfissionalDTO;
import com.fatec.estudamaisbackend.entity.ExperienciaProfissional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExperienciaProfissionalMapper {

    // Entidade -> DTO
    // MapStruct mapeia cargo->cargo, instituicao->instituicao automaticamente.
    // Só precisamos ajudar no relacionamento do Professor:
    @Mapping(source = "professor.id", target = "idProfessor")
    ExperienciaProfissionalDTO toDto(ExperienciaProfissional e);

    // DTO -> Entidade
    @Mapping(target = "professor", ignore = true) // O Service define o professor
    @Mapping(target = "id", ignore = true)        // O Banco gera o ID
    ExperienciaProfissional toEntity(ExperienciaProfissionalDTO dto);

    List<ExperienciaProfissionalDTO> toDtoList(List<ExperienciaProfissional> lista);
}