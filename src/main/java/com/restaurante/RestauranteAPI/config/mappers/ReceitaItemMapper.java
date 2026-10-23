package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.ReceitaItemRequest;
import com.restaurante.RestauranteAPI.dto.response.ReceitaItemResponse;
import com.restaurante.RestauranteAPI.entities.ReceitaItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReceitaItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "ingrediente", ignore = true)
    ReceitaItem toEntity(ReceitaItemRequest request);

    @Mapping(target = "produtoId", source = "produto.id")
    @Mapping(target = "nomeProduto", source = "produto.nome")
    @Mapping(target = "ingredienteId", source = "ingrediente.id")
    @Mapping(target = "nomeIngrediente", source = "ingrediente.nome")
    @Mapping(target = "unidadeMedida", source = "ingrediente.unidadeMedida")
    ReceitaItemResponse toResponse(ReceitaItem receitaItem);

}
