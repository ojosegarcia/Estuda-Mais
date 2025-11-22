package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.dtos.AlunoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AulaResumoMapper.class, FeedbackMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlunoMapper {

    @Mapping(source = "educationLevel", target = "escolaridade")
    @Mapping(source = "interesse", target = "interesse")
    @Mapping(source = "aulas", target = "aulas")
    @Mapping(source = "feedbacks", target = "feedbacks")
    AlunoDTO toDto(Aluno aluno);

    // reverse: map simple fields only; relations should be handled by services
    @Mapping(source = "escolaridade", target = "educationLevel")
    @Mapping(target = "aulas", ignore = true)
    @Mapping(target = "feedbacks", ignore = true)
    Aluno toEntity(AlunoDTO dto);

    List<AlunoDTO> toDtoList(List<Aluno> alunos);
}