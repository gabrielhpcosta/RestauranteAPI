package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelarComandaRequest {

    @NotNull
    private String motivo;

}
