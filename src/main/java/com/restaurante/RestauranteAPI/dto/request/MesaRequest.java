package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MesaRequest {


    @NotNull(message = "É obrigatório informar o número da mesa")
    @Positive(message = "Os números das mesas são acima de zero")
    private Integer numero;

    @NotNull(message = "É obrigatório informar a capacidade da mesa")
    @Positive(message = "A capacidade da mesa deve ser maior que zero")
    private Integer capacidade;

}
