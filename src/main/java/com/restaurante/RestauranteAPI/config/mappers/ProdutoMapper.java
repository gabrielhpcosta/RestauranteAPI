package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.ProdutoRequest;
import com.restaurante.RestauranteAPI.dto.response.ProdutoResponse;
import com.restaurante.RestauranteAPI.entities.Produto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "disponivel", ignore = true)
    Produto toEntity(ProdutoRequest request);

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "nomeCategoria", source = "categoria.nome")
    ProdutoResponse toResponse(Produto produto);


}
