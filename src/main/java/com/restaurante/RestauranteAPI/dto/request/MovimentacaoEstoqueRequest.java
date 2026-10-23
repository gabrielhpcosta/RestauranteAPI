package com.restaurante.RestauranteAPI.dto.request;

import com.restaurante.RestauranteAPI.enums.TipoMovimentacao;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MovimentacaoEstoqueRequest {

    @NotNull(message = "O ID do ingrediente é obrigatório")
    private Long ingredienteId;

    @NotNull(message = "É obrigatório informar o tipo")
    private TipoMovimentacao tipo;

    @NotNull(message = "Informe a quantidade")
    @Positive(message = "A quantidade deve ser maior que zero")
    @Digits(integer = 12, fraction = 3)
    private BigDecimal quantidade;

    @NotBlank(message = "É obrigatório informar o motivo")
    @Size(max = 1000, message = "O número de caracteres não pode ser superior a 1000")
    private String motivo;

}
