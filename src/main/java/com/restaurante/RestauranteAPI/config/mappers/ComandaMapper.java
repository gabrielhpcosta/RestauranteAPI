package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.ComandaRequest;
import com.restaurante.RestauranteAPI.dto.response.ComandaResponse;
import com.restaurante.RestauranteAPI.entities.Comanda;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ComandaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mesa", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "abertaPor", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "abertaEm", ignore = true)
    @Mapping(target = "encerradaEm", ignore = true)
    Comanda toEntity(ComandaRequest request);
    @Mapping(target = "mesaId", source = "mesa.id")
    @Mapping(target = "numeroMesa", source = "mesa.numero")
    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "nomeCliente", source = "cliente.nome")
    @Mapping(target = "abertaPorId", source = "abertaPor.id")
    ComandaResponse toResponse(Comanda comanda);


}
