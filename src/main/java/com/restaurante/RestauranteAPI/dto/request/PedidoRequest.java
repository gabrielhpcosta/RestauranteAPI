package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PedidoRequest {

    @NotNull(message = "Informe o ID da comanda")
    private Long comandaId;

    @Size(max = 1000, message = "O limite máximo de caracteres é 1000")
    private String observacao;

    @NotEmpty(message = "O pedido precisa ter no mínimo um item")
    @Valid
    private List<ItemPedidoRequest> itens;

}
