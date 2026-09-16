package com.restaurante.RestauranteAPI.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MesaResponse {

    private Long id;

    private Integer numero;

    private Integer capacidade;

    private Boolean disponivel = true;

}
