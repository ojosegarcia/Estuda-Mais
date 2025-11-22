package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.dtos.ProfessorResumoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfessorResumoMapper {

    @Mapping(source = "nomeCompleto", target = "nomeCompleto")
    @Mapping(source = "valorHora", target = "valorHora")
    @Mapping(source = "imagemCertificado", target = "fotoCertificado")
    ProfessorResumoDTO toDto(Professor p);
}