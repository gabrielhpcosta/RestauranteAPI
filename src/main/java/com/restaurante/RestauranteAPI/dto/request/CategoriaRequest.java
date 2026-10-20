package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequest {

    @NotBlank(message = "É obrigatório informar a categoria")
    private String nome;

}
