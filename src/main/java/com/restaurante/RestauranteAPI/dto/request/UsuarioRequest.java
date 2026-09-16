package com.restaurante.RestauranteAPI.dto.request;

import com.restaurante.RestauranteAPI.enums.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequest {

    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;

    @NotBlank(message = "O campo E-mail é obrigatório")
    @Email(message = "O e-mail é inválido")
    private String email;

    @NotBlank(message = "O campo Senha é obrigatório")
    @lombok.ToString.Exclude
    private String senha;

    @jakarta.validation.constraints.NotNull
    private Users role;

    @jakarta.validation.constraints.NotNull
    private Boolean ativo = true;


}
