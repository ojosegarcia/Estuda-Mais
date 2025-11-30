package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.dtos.FeedbackDTO;
import com.fatec.estudamaisbackend.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedbackMapper {

    // Entidade -> DTO
    @Mapping(source = "aula.id", target = "idAula")
    // idAluno e idProfessor são mapeados automaticamente (Long -> Long)
    FeedbackDTO toDto(Feedback f);

    // DTO -> Entidade
    @Mapping(target = "aula", ignore = true) // O Service busca a aula pelo ID
    @Mapping(target = "id", ignore = true)   // ID é gerado pelo banco
    Feedback toEntity(FeedbackDTO dto);
}