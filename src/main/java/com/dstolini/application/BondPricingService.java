package com.dstolini.application;

import com.dstolini.domain.engine.PricingEngine;
import com.dstolini.domain.model.Bond;
import com.dstolini.domain.model.PricingResult;
import com.dstolini.domain.port.in.BondPricingUseCase;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
class BondPricingService implements BondPricingUseCase {

    private final PricingEngine engine;

    BondPricingService(PricingEngine engine) {
        this.engine = engine;
    }

    @Override
    public PricingResult calculateYtm(Bond bond, BigDecimal cleanPrice, LocalDate settlementDate) {
        BigDecimal ytm      = engine.yieldToMaturity(bond, cleanPrice, settlementDate);
        BigDecimal accrued  = engine.accruedInterest(bond, settlementDate);
        BigDecimal dirty    = cleanPrice.add(accrued);
        return new PricingResult(ytm, cleanPrice, dirty, accrued, settlementDate);
    }

    @Override
    public PricingResult calculatePrice(Bond bond, BigDecimal yield, LocalDate settlementDate) {
        BigDecimal dirty    = engine.dirtyPrice(bond, yield, settlementDate);
        BigDecimal accrued  = engine.accruedInterest(bond, settlementDate);
        BigDecimal clean    = dirty.subtract(accrued);
        return new PricingResult(yield, clean, dirty, accrued, settlementDate);
    }
}
