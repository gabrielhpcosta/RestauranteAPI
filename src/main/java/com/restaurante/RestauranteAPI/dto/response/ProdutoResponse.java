package com.restaurante.RestauranteAPI.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProdutoResponse {

    private Long id;

    private Long categoriaId;

    private String nomeCategoria;

    private String nome;

    private String descricao;

    private BigDecimal preco;

    private Boolean disponivel;

    private Boolean exigePreparo;

}
