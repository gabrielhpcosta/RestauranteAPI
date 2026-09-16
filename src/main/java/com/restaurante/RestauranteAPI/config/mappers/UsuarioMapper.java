package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.UsuarioRequest;
import com.restaurante.RestauranteAPI.dto.response.UsuarioResponse;
import com.restaurante.RestauranteAPI.entities.Usuario;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "tentativasFalhas", ignore = true)
    @Mapping(target = "bloqueadoAte", ignore = true)
    Usuario toEntity(UsuarioRequest request);

    UsuarioResponse toResponse(Usuario usuario);
}
