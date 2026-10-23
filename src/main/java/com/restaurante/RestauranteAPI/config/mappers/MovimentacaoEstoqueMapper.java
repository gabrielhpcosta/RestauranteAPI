package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.MovimentacaoEstoqueRequest;
import com.restaurante.RestauranteAPI.dto.response.MovimentacaoEstoqueResponse;
import com.restaurante.RestauranteAPI.entities.MovimentacaoEstoque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovimentacaoEstoqueMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ingrediente", ignore = true)
    @Mapping(target = "itemPedido", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    MovimentacaoEstoque toEntity(MovimentacaoEstoqueRequest request);

    @Mapping(target = "ingredienteId", source = "ingrediente.id")
    @Mapping(target = "nomeIngrediente", source = "ingrediente.nome")
    @Mapping(target = "itemPedidoId", source = "itemPedido.id")
    @Mapping(target = "responsavelId", source = "responsavel.id")
    MovimentacaoEstoqueResponse toResponse(MovimentacaoEstoque movimentacaoEstoque);

}
