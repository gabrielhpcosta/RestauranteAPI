package com.restaurante.RestauranteAPI.dto.response;

import com.restaurante.RestauranteAPI.enums.FormaPagamento;
import com.restaurante.RestauranteAPI.enums.StatusPagamento;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class PagamentoResponse {

    private Long id;

    private Long comandaId;

    private BigDecimal valor;

    private StatusPagamento status;

    private String chaveIdempotencia;

    private Long registradoPorId;

    private OffsetDateTime criadoEm;

    private OffsetDateTime estornadoEm;

    private Long estornadoPorId;

    private String motivoEstorno;

    private FormaPagamento forma;

}
