package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Feedback;
import com.fatec.estudamaisbackend.dtos.FeedbackDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedbackMapper {

    @Mapping(source = "aula.id", target = "idAula")
    @Mapping(source = "aluno.id", target = "idAluno")
    @Mapping(source = "professor.id", target = "idProfessor")
    FeedbackDTO toDto(Feedback f);

    // DTO -> Entity: ignore nested relations (service resolves them)
    @Mapping(target = "aula", ignore = true)
    @Mapping(target = "aluno", ignore = true)
    @Mapping(target = "professor", ignore = true)
    Feedback toEntity(FeedbackDTO dto);
}