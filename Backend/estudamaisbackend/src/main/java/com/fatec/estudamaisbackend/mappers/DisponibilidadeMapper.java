package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.dtos.DisponibilidadeDTO;
import com.fatec.estudamaisbackend.entity.Disponibilidade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DisponibilidadeMapper {

    // Entidade -> DTO
    // Extrai o ID de dentro do objeto professor
    @Mapping(source = "professor.id", target = "idProfessor")
    DisponibilidadeDTO toDto(Disponibilidade entidade);

    // DTO -> Entidade
    // Ignoramos o professor aqui porque o Service busca ele no banco pelo ID
    @Mapping(target = "professor", ignore = true)
    Disponibilidade toEntity(DisponibilidadeDTO dto);

    List<DisponibilidadeDTO> toDtoList(List<Disponibilidade> lista);
}