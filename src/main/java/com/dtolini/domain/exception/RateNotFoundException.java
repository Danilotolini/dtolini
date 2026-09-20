package com.dtolini.domain.exception;

/** Lançada quando nenhuma taxa é encontrada no cache local para o CUSIP pedido. */
public class RateNotFoundException extends RuntimeException {

    public RateNotFoundException(String cusip) {
        super("Taxa não encontrada para CUSIP: " + cusip);
    }
}
