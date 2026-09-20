package com.dstolini.application;

import com.dstolini.domain.engine.PricingEngine;
import com.dstolini.domain.model.Bond;
import com.dstolini.domain.model.PricingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * CAMADA: serviço de aplicação. Usa Mockito para isolar o PricingEngine.
 *
 * O que BondPricingService promete:
 *   1. Delegar o cálculo matemático ao PricingEngine (nunca fazer math aqui)
 *   2. Combinar os resultados corretamente:
 *      - calculateYtm:   dirtyPrice = cleanPrice + accruedInterest (identidade contábil)
 *      - calculatePrice: cleanPrice = dirtyPrice  - accruedInterest
 *
 * Esses invariantes são verificáveis sem precisar que o engine esteja implementado.
 * Quando você implementar o PricingEngine, esses testes continuam verdes sem mudança.
 */
@ExtendWith(MockitoExtension.class)
class BondPricingServiceTest {

    @Mock
    PricingEngine engine;

    BondPricingService service;

    private static final Bond BOND = Bond.treasury(
            "912810RV0", "5Y Note", new BigDecimal("0.045"),
            LocalDate.of(2029, 8, 31), LocalDate.of(2024, 8, 31));
    private static final LocalDate SETTLEMENT = LocalDate.of(2024, 9, 3);

    @BeforeEach
    void setUp() {
        service = new BondPricingService(engine);
    }

    @Test
    void calculateYtm_delegates_to_engine_and_computes_dirty_price() {
        BigDecimal cleanPrice = new BigDecimal("98.75");
        BigDecimal ytm       = new BigDecimal("0.0512");
        BigDecimal accrued   = new BigDecimal("0.37");

        when(engine.yieldToMaturity(BOND, cleanPrice, SETTLEMENT)).thenReturn(ytm);
        when(engine.accruedInterest(BOND, SETTLEMENT)).thenReturn(accrued);

        PricingResult result = service.calculateYtm(BOND, cleanPrice, SETTLEMENT);

        // dirtyPrice é a soma contábil — o service não pode inventar outro valor
        assertThat(result.dirtyPrice())
                .isEqualByComparingTo(cleanPrice.add(accrued));

        assertThat(result.yieldToMaturity()).isEqualByComparingTo(ytm);
        assertThat(result.cleanPrice()).isEqualByComparingTo(cleanPrice);
        assertThat(result.accruedInterest()).isEqualByComparingTo(accrued);
        assertThat(result.settlementDate()).isEqualTo(SETTLEMENT);

        verify(engine).yieldToMaturity(BOND, cleanPrice, SETTLEMENT);
        verify(engine).accruedInterest(BOND, SETTLEMENT);
    }

    @Test
    void calculatePrice_delegates_to_engine_and_computes_clean_price() {
        BigDecimal yield   = new BigDecimal("0.0512");
        BigDecimal dirty   = new BigDecimal("99.12");
        BigDecimal accrued = new BigDecimal("0.37");

        when(engine.dirtyPrice(BOND, yield, SETTLEMENT)).thenReturn(dirty);
        when(engine.accruedInterest(BOND, SETTLEMENT)).thenReturn(accrued);

        PricingResult result = service.calculatePrice(BOND, yield, SETTLEMENT);

        // cleanPrice é a diferença contábil — o service não pode inventar outro valor
        assertThat(result.cleanPrice())
                .isEqualByComparingTo(dirty.subtract(accrued));

        assertThat(result.yieldToMaturity()).isEqualByComparingTo(yield);
        assertThat(result.dirtyPrice()).isEqualByComparingTo(dirty);
        assertThat(result.accruedInterest()).isEqualByComparingTo(accrued);

        verify(engine).dirtyPrice(BOND, yield, SETTLEMENT);
        verify(engine).accruedInterest(BOND, SETTLEMENT);
    }
}
