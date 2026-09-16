package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.MesaRequest;
import com.restaurante.RestauranteAPI.dto.response.MesaResponse;
import com.restaurante.RestauranteAPI.entities.Mesa;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MesaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "disponivel", ignore = true)
    Mesa toEntity(MesaRequest request);

    MesaResponse toResponse(Mesa mesa);
}
