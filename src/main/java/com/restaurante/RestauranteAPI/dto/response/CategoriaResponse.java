package com.restaurante.RestauranteAPI.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaResponse {

    private Long id;

    private String nome;

    private Boolean ativo;

}
