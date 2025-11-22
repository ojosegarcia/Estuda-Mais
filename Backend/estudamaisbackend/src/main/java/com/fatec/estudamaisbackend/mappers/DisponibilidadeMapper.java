package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Disponibilidade;
import com.fatec.estudamaisbackend.dtos.DisponibilidadeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DisponibilidadeMapper {

    @Mapping(source = "dayOfWeek", target = "diaSemana")
    @Mapping(source = "startTime", target = "horarioInicio")
    @Mapping(source = "endTime", target = "horarioFim")
    @Mapping(source = "active", target = "ativo")
    DisponibilidadeDTO toDto(Disponibilidade d);

    @Mapping(source = "diaSemana", target = "dayOfWeek")
    @Mapping(source = "horarioInicio", target = "startTime")
    @Mapping(source = "horarioFim", target = "endTime")
    @Mapping(source = "ativo", target = "active")
    Disponibilidade toEntity(DisponibilidadeDTO dto);
}