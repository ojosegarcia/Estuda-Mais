package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.dtos.AlunoDTO;
import com.fatec.estudamaisbackend.entity.Aluno;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlunoMapper {

    // REMOVEMOS O MAPEAMENTO DE AULAS E FEEDBACKS AQUI
    // O Frontend vai buscar isso em endpoints separados: /alunos/{id}/aulas
    
    AlunoDTO toDto(Aluno aluno);

    @Mapping(target = "id", ignore = true)
    Aluno toEntity(AlunoDTO dto);

    List<AlunoDTO> toDtoList(List<Aluno> alunos);
}