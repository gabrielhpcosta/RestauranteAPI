package com.restaurante.RestauranteAPI.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ClienteResponse {

    private Long id;

    private String nome;

    private String cpf;

    private String telefone;

    private String email;

    private String cep;

    private String logradouro;

    private String bairro;

    private String cidade;

    private String uf;

    private Boolean ativo;

}
