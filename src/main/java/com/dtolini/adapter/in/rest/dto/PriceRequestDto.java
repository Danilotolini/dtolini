package com.dtolini.adapter.in.rest.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Entrada para POST /api/v1/pricing/price — calcula preço a partir do YTM. */
public record PriceRequestDto(

    @NotBlank
    String cusip,

    @NotNull
    @DecimalMin("0.0")
    BigDecimal couponRate,

    @NotNull
    LocalDate maturityDate,

    @NotNull
    LocalDate issueDate,

    @NotNull
    @DecimalMin(value = "-0.10", message = "yield mínimo é -10% (ADR-004)")
    @DecimalMax(value = "1.00",  message = "yield máximo é 100% (ADR-004)")
    BigDecimal yieldToMaturity,     // ex: 0.0462 para 4,62%

    @NotNull
    LocalDate settlementDate
) {}
