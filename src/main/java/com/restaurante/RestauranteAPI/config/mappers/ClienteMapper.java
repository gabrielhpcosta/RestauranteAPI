package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.ClienteRequest;
import com.restaurante.RestauranteAPI.dto.response.ClienteResponse;
import com.restaurante.RestauranteAPI.entities.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente cliente);
}