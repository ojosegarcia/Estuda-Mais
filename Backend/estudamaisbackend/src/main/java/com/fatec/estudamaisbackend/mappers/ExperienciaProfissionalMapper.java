package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.ExperienciaProfissional;
import com.fatec.estudamaisbackend.dtos.ExperienciaProfissionalDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExperienciaProfissionalMapper {

    @Mapping(source = "position", target = "cargo")
    @Mapping(source = "institution", target = "instituicao")
    @Mapping(source = "period", target = "periodo")
    @Mapping(source = "description", target = "descricao")
    ExperienciaProfissionalDTO toDto(ExperienciaProfissional e);

    @Mapping(source = "cargo", target = "position")
    @Mapping(source = "instituicao", target = "institution")
    @Mapping(source = "periodo", target = "period")
    @Mapping(source = "descricao", target = "description")
    ExperienciaProfissional toEntity(ExperienciaProfissionalDTO dto);
}