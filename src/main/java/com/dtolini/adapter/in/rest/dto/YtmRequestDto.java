package com.dtolini.adapter.in.rest.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Entrada para POST /api/v1/pricing/ytm — calcula YTM a partir do preço limpo. */
public record YtmRequestDto(

    @NotBlank
    String cusip,

    @NotNull
    @DecimalMin("0.0")
    BigDecimal couponRate,          // ex: 0.045 para 4,5%

    @NotNull
    LocalDate maturityDate,

    @NotNull
    LocalDate issueDate,

    @NotNull
    @DecimalMin(value = "0.01", message = "cleanPrice deve ser positivo")
    @DecimalMax(value = "200.0", message = "cleanPrice não pode exceder 200% do par")
    BigDecimal cleanPrice,          // % do par — ex: 98.75

    @NotNull
    LocalDate settlementDate
) {}
