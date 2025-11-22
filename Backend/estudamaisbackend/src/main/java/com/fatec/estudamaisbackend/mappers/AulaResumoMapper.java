package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Aula;
import com.fatec.estudamaisbackend.dtos.AulaResumoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AulaResumoMapper {

    @Mapping(source = "professor.id", target = "idProfessor")
    @Mapping(source = "aluno.id", target = "idAluno")
    @Mapping(source = "materia.id", target = "idMateria")
    @Mapping(source = "dataAula", target = "dataAula")
    @Mapping(source = "horarioInicio", target = "horarioInicio")
    @Mapping(source = "horarioFim", target = "horarioFim")
    @Mapping(target = "statusAula", expression = "java(aula.getStatus() != null ? aula.getStatus().name() : null)")
    @Mapping(source = "classValue", target = "valorAula")
    AulaResumoDTO toDto(Aula aula);
}