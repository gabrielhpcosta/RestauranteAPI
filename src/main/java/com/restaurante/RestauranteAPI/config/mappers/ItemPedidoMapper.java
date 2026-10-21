package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.ItemPedidoRequest;
import com.restaurante.RestauranteAPI.dto.response.ItemPedidoResponse;
import com.restaurante.RestauranteAPI.entities.ItemPedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "nomeProduto", ignore = true)
    @Mapping(target = "precoUnitario", ignore = true)
    @Mapping(target = "exigePreparo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    ItemPedido toEntity(ItemPedidoRequest request);

    @Mapping(target = "produtoId", source = "produto.id")
    ItemPedidoResponse toResponse(ItemPedido itemPedido);

}
