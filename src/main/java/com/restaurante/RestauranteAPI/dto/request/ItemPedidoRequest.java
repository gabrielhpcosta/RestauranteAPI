package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemPedidoRequest {

    @NotNull(message = "Informe o ID do produto")
    private Long produtoId;

    @NotNull(message = "Informe a quantidade")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantidade;

    @Size(max = 1000, message = "O limite máximo de caracteres é 1000")
    private String observacao;

}
