package com.restaurante.RestauranteAPI.dto.request;

import com.restaurante.RestauranteAPI.enums.StatusItemPedido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlterarStatusItemRequest {

    @NotNull(message = "Informe o novo status")
    private StatusItemPedido status;

    @Size(max = 1000, message = "O motivo pode ter no máximo 1000 caracteres")
    private String motivo;
}
