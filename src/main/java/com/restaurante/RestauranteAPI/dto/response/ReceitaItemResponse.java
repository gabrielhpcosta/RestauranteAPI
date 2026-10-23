package com.restaurante.RestauranteAPI.dto.response;

import com.restaurante.RestauranteAPI.enums.UnidadeMedida;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReceitaItemResponse {

    private Long id;
    private Long produtoId;
    private Long ingredienteId;
    private String nomeIngrediente;
    private BigDecimal quantidade;
    private UnidadeMedida unidadeMedida;
    private String nomeProduto;

}
