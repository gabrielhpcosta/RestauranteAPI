package com.restaurante.RestauranteAPI.dto.response;

import com.restaurante.RestauranteAPI.enums.StatusItemPedido;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemPedidoResponse {

    private Long id;

    private Long produtoId;

    private String nomeProduto;

    private Integer quantidade;

    private BigDecimal precoUnitario;

    private Boolean exigePreparo;

    private String observacao;

    private StatusItemPedido status;

}
