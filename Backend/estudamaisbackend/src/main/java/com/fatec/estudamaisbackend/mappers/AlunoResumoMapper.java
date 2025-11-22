package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.dtos.AlunoResumoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlunoResumoMapper {

    @Mapping(source = "nomeCompleto", target = "nomeCompleto")
    AlunoResumoDTO toDto(Aluno a);
}