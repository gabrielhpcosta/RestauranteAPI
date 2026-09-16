package com.restaurante.RestauranteAPI.exceptions;

public class ServicoIndisponivelException extends RuntimeException {
    public ServicoIndisponivelException(String message) {
        super(message);
    }
}
