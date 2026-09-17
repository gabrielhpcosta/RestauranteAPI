package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComandaRequest {

    @NotNull(message = "Informe a mesa")
    @Positive(message = "O ID da mesa deve ser positivo")
    private Long mesaId;

    @Positive(message = "O ID do cliente deve ser positivo")
    private Long clienteId;
}

