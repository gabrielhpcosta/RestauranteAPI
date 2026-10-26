package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelarComandaRequest {

    @NotBlank(message = "Informe o motivo do cancelamento")
    @Size(max = 255, message = "O motivo pode ter no máximo 255 caracteres")
    private String motivo;

}
