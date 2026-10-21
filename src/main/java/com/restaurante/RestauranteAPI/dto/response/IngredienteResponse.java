package com.restaurante.RestauranteAPI.dto.response;

import com.restaurante.RestauranteAPI.enums.UnidadeMedida;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class IngredienteResponse {

    private Long id;

    private String nome;

    private UnidadeMedida unidadeMedida;

    private BigDecimal saldo;

    private BigDecimal estoqueMinimo;

    private Boolean ativo;

}
