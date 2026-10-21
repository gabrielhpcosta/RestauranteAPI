package com.restaurante.RestauranteAPI.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class PedidoResponse {

    private Long id;

    private Long comandaId;

    private Long registradoPorId;

    private String observacao;

    private OffsetDateTime criadoEm;

    private List<ItemPedidoResponse> itens;

    private BigDecimal total;

}
