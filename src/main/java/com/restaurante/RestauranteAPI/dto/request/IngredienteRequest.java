package com.restaurante.RestauranteAPI.dto.request;

import com.restaurante.RestauranteAPI.enums.UnidadeMedida;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredienteRequest {

    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;

    @NotNull(message = "A unidade de medida é obrigatória")
    private UnidadeMedida unidadeMedida;

    @NotNull(message = "O estoque mínimo é obrigatório")
    @PositiveOrZero(message = "O estoque mínimo não pode ser negativo")
    @Digits(integer = 12, fraction = 3)
    private BigDecimal estoqueMinimo;

}
