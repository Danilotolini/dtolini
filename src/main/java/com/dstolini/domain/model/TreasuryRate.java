package com.dstolini.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Taxa de mercado de um Treasury, conforme publicado pela Treasury Fiscal Data API. */
public record TreasuryRate(
    String cusip,
    String securityType,
    String description,
    LocalDate maturityDate,
    BigDecimal couponRate,
    BigDecimal yield,
    LocalDate rateDate
) {}
