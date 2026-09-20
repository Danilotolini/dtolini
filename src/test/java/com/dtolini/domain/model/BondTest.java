package com.dtolini.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * CAMADA: domínio puro. Zero Spring, zero I/O.
 *
 * O que um test de domínio prova: que o modelo não carrega suposições erradas.
 * Bond é um record, então o compilador garante imutabilidade e igualdade por valor.
 * O que resta testar é o comportamento do factory method — ele emite o objeto certo?
 */
class BondTest {

    private static final LocalDate MATURITY  = LocalDate.of(2029, 8, 31);
    private static final LocalDate ISSUE     = LocalDate.of(2024, 8, 31);
    private static final BigDecimal COUPON   = new BigDecimal("0.045");

    @Test
    void treasury_factory_sets_standard_us_treasury_defaults() {
        Bond bond = Bond.treasury("912810RV0", "5Y Note", COUPON, MATURITY, ISSUE);

        assertThat(bond.faceValue()).isEqualByComparingTo("100");
        assertThat(bond.paymentFrequency()).isEqualTo(2);  // semestral — padrão do Tesouro americano
    }

    @Test
    void treasury_factory_preserves_all_provided_fields() {
        Bond bond = Bond.treasury("912810RV0", "5Y Note", COUPON, MATURITY, ISSUE);

        assertThat(bond.cusip()).isEqualTo("912810RV0");
        assertThat(bond.description()).isEqualTo("5Y Note");
        assertThat(bond.couponRate()).isEqualByComparingTo(COUPON);
        assertThat(bond.maturityDate()).isEqualTo(MATURITY);
        assertThat(bond.issueDate()).isEqualTo(ISSUE);
    }

    @Test
    void full_constructor_accepts_custom_face_value_and_frequency() {
        Bond bond = new Bond("X", "Corp Bond", BigDecimal.valueOf(0.06),
                             MATURITY, ISSUE, BigDecimal.valueOf(1000), 4);

        assertThat(bond.faceValue()).isEqualByComparingTo("1000");
        assertThat(bond.paymentFrequency()).isEqualTo(4);
    }
}
