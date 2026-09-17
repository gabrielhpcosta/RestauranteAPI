package com.restaurante.RestauranteAPI.dto.response;

import com.restaurante.RestauranteAPI.enums.StatusComanda;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ComandaResponse {

    private Long id;
    private Long mesaId;
    private Integer numeroMesa;
    private Long clienteId;
    private String nomeCliente;
    private Long abertaPorId;
    private StatusComanda status;
    private OffsetDateTime abertaEm;
    private OffsetDateTime encerradaEm;
}