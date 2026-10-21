package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.PedidoRequest;
import com.restaurante.RestauranteAPI.dto.response.PedidoResponse;
import com.restaurante.RestauranteAPI.entities.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comanda", ignore = true)
    @Mapping(target = "registradoPor", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    Pedido toEntity(PedidoRequest request);

    @Mapping(target = "comandaId", source = "comanda.id")
    @Mapping(target = "registradoPorId", source = "registradoPor.id")
    @Mapping(target = "itens", ignore = true)
    @Mapping(target = "total", ignore = true)
    PedidoResponse toResponse(Pedido pedido);

}
