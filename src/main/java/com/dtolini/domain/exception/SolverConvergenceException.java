package com.dtolini.domain.exception;

import java.math.BigDecimal;

/** Lançada quando o solver de bisseção não encontra raiz no intervalo de busca (ADR-004). */
public class SolverConvergenceException extends RuntimeException {

    public SolverConvergenceException(BigDecimal cleanPrice, BigDecimal low, BigDecimal high) {
        super(String.format(
                "Nenhuma raiz encontrada para cleanPrice=%.4f no intervalo [%.2f, %.2f]. " +
                "Verifique se o preço de mercado está dentro de um range razoável.",
                cleanPrice, low, high));
    }
}
