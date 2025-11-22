package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Conquista;
import com.fatec.estudamaisbackend.dtos.ConquistaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConquistaMapper {

    ConquistaDTO toDto(Conquista c);

    // dto->entity: service sets professor
    Conquista toEntity(ConquistaDTO dto);
}