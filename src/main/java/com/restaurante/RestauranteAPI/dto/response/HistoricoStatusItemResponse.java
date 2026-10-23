package com.restaurante.RestauranteAPI.dto.response;

import com.restaurante.RestauranteAPI.enums.StatusItemPedido;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class HistoricoStatusItemResponse {

    private Long id;

    private Long itemPedidoId;

    private StatusItemPedido statusAnterior;

    private StatusItemPedido statusNovo;

    private Long alteradoPorId;

    private OffsetDateTime alteradoEm;

    private String motivo;

}
