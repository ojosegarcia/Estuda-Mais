package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Aula;
import com.fatec.estudamaisbackend.dtos.AulaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProfessorResumoMapper.class, AlunoResumoMapper.class, MateriaMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AulaMapper {

    @Mapping(source = "professor.id", target = "idProfessor")
    @Mapping(source = "aluno.id", target = "idAluno")
    @Mapping(source = "materia.id", target = "idMateria")
    @Mapping(target = "statusAula", expression = "java(aula.getStatus() != null ? aula.getStatus().name() : null)")
    @Mapping(source = "meetingLink", target = "linkReuniao")
    @Mapping(source = "classValue", target = "valorAula")
    @Mapping(source = "createdAt", target = "dataCriacao")
    AulaDTO toDto(Aula aula);

    List<AulaDTO> toDtoList(List<Aula> aulas);
}