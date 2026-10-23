package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EstornarPagamentoRequest {

    @NotBlank(message = "Informe o motivo do estorno")
    @Size(max = 1000, message = "O motivo pode ter no máximo 1000 caracteres")
    private String motivo;

}
