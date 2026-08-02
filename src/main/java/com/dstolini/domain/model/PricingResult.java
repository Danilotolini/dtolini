package com.dstolini.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Resultado completo de uma precificação: yield, preço limpo, sujo e juro acumulado. */
public record PricingResult(
    BigDecimal yieldToMaturity,
    BigDecimal cleanPrice,
    BigDecimal dirtyPrice,
    BigDecimal accruedInterest,
    LocalDate settlementDate
) {}
