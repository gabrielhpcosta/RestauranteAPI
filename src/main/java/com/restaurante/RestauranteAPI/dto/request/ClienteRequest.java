package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @Size(max = 20, message = "O telefone deve ter até 20 caracteres")
    private String telefone;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Informe um email valido")
    @Size(max = 255, message = "O email deve ter até 255 caracteres")
    private String email;

    @Size(max = 10, message = "O CEP deve ter até 10 caracteres")
    @NotBlank(message = "O campo CEP é obrigatório")
    private String cep;

}
