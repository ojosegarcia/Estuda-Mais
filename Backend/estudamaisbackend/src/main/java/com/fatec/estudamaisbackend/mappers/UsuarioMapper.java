package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.entity.Usuario;
import com.fatec.estudamaisbackend.dtos.UsuarioDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    // entidade -> DTO
    // imagemPerfil (entity) -> fotoPerfil (dto)
    // criadoEm (EntidadeBase) -> dataCadastro (dto)
    @Mapping(source = "imagemPerfil", target = "fotoPerfil")
    @Mapping(source = "criadoEm", target = "dataCadastro")
    UsuarioDTO toDto(Usuario u);

    // NOTA: não mapeamos DTO -> Usuario (classe abstrata). Services devem montar a entidade concreta.
}