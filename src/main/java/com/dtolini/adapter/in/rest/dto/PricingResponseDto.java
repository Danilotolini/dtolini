package com.dtolini.adapter.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PricingResponseDto(
    BigDecimal yieldToMaturity,
    BigDecimal cleanPrice,
    BigDecimal dirtyPrice,
    BigDecimal accruedInterest,
    LocalDate settlementDate
) {}
