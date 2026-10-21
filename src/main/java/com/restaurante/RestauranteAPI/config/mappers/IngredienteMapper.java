package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.IngredienteRequest;
import com.restaurante.RestauranteAPI.dto.response.IngredienteResponse;
import com.restaurante.RestauranteAPI.entities.Ingrediente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "saldo", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    Ingrediente toEntity(IngredienteRequest request);

    IngredienteResponse toResponse(Ingrediente ingrediente);

}
