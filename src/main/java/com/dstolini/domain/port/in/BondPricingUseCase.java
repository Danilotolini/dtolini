package com.dstolini.domain.port.in;

import com.dstolini.domain.model.Bond;
import com.dstolini.domain.model.PricingResult;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Porta de entrada: casos de uso de precificação expostos ao adaptador REST. */
public interface BondPricingUseCase {

    /** Dado um preço limpo, retorna YTM + preço sujo + juro acumulado. */
    PricingResult calculateYtm(Bond bond, BigDecimal cleanPrice, LocalDate settlementDate);

    /** Dado um YTM, retorna preço limpo + sujo + juro acumulado. */
    PricingResult calculatePrice(Bond bond, BigDecimal yieldToMaturity, LocalDate settlementDate);
}
