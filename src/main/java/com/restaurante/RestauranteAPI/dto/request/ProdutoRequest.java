package com.restaurante.RestauranteAPI.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequest {

    @NotNull(message = "Informe a categoria do produto")
    private Long categoriaId;

    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;

    @Size(max = 1000, message = "A descrição pode ter no máximo 1000 caracteres")
    private String descricao;

    @NotNull(message = "O campo preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal preco;

    @NotNull(message = "Informe se o produto exige preparo")
    private Boolean exigePreparo;
}
