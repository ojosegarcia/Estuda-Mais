package com.fatec.estudamaisbackend.mappers;

import com.fatec.estudamaisbackend.dtos.UsuarioDTO;
import com.fatec.estudamaisbackend.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    // A entidade agora já se chama fotoPerfil e dataCadastro, igual ao DTO/Front.
    // O MapStruct faz o match automático por nome.
    UsuarioDTO toDto(Usuario u);
}