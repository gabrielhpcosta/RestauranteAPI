package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.CategoriaRequest;
import com.restaurante.RestauranteAPI.dto.response.CategoriaResponse;
import com.restaurante.RestauranteAPI.entities.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    Categoria toEntity(CategoriaRequest request);

    CategoriaResponse toResponse(Categoria categoria);

}
