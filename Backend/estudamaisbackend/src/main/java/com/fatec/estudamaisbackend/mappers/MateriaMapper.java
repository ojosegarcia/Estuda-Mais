package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Materia;
import com.fatec.estudamaisbackend.dtos.MateriaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MateriaMapper {
    MateriaDTO toDto(Materia entity);
    Materia toEntity(MateriaDTO dto);
}