package com.restaurante.RestauranteAPI.config.mappers;

import com.restaurante.RestauranteAPI.dto.request.PagamentoRequest;
import com.restaurante.RestauranteAPI.dto.response.PagamentoResponse;
import com.restaurante.RestauranteAPI.entities.Pagamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PagamentoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comanda", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "registradoPor", ignore = true)
    @Mapping(target = "estornadoEm", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "estornadoPor", ignore = true)
    @Mapping(target = "motivoEstorno", ignore = true)
    Pagamento toEntity(PagamentoRequest request);

    @Mapping(target = "comandaId", source = "comanda.id")
    @Mapping(target = "registradoPorId", source = "registradoPor.id")
    @Mapping(target = "estornadoPorId", source = "estornadoPor.id")
    PagamentoResponse toResponse(Pagamento pagamento);

}
