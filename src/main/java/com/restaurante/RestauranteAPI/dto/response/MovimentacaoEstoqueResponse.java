package com.restaurante.RestauranteAPI.dto.response;

import com.restaurante.RestauranteAPI.enums.TipoMovimentacao;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class MovimentacaoEstoqueResponse {

    private Long id;
    private Long ingredienteId;
    private String nomeIngrediente;
    private TipoMovimentacao tipo;
    private BigDecimal quantidade;
    private String motivo;
    private OffsetDateTime criadoEm;
    private Long itemPedidoId;
    private Long responsavelId;

}
