package com.restaurante.RestauranteAPI.dto.response;

import com.restaurante.RestauranteAPI.enums.Users;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponse {

    private Long id;

    private String nome;

    private String email;

    private Users role;

}
