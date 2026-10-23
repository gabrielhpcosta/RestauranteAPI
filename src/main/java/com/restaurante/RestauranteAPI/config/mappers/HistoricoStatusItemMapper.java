package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.response.HistoricoStatusItemResponse;
import com.restaurante.RestauranteAPI.entities.HistoricoStatusItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoricoStatusItemMapper {

    @Mapping(target = "itemPedidoId", source = "itemPedido.id")
    @Mapping(target = "alteradoPorId", source = "alteradoPor.id")
    HistoricoStatusItemResponse toResponse(HistoricoStatusItem historicoStatusItem);

}
