package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReceitaItemRequest {

    @NotNull(message = "Informe o ID do produto")
    private Long produtoId;

    @NotNull(message = "Informe o ID do ingrediente")
    private Long ingredienteId;

    @NotNull(message = "Informe a quantidade")
    @Positive(message = "A quantidade deve ser maior que zero")
    @Digits(integer = 12, fraction = 3)
    private BigDecimal quantidade;

}
