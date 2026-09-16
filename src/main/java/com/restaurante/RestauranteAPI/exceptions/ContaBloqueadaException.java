package com.restaurante.RestauranteAPI.exceptions;

public class ContaBloqueadaException extends RuntimeException {
    public ContaBloqueadaException(String message) {
        super(message);
    }
}
