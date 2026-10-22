package com.restaurante.RestauranteAPI.dto.request;

import com.restaurante.RestauranteAPI.enums.FormaPagamento;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagamentoRequest {

    @NotNull(message = "Informe a comanda")
    private Long comandaId;

    @NotNull(message = "Informe o valor")
    @Positive(message = "O valor precisa ser maior que zero")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal valor;

    @NotNull(message = "Informe a forma de pagamento")
    private FormaPagamento forma;

    @NotBlank(message = "Informe a chave Idempotencia")
    @Size(max = 100)
    private String chaveIdempotencia;
}
