package com.dtolini.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa um título do Tesouro americano.
 *
 * Todos os Treasuries são semi-anuais (paymentFrequency = 2) e usam
 * a convenção Actual/Actual (ICMA) para day-count. Preços são expressos
 * como percentual do valor de face (faceValue = 100).
 */
public record Bond(
    String cusip,
    String description,
    BigDecimal couponRate,      // taxa anual — ex: 0.045 para 4,5%
    LocalDate maturityDate,
    LocalDate issueDate,
    BigDecimal faceValue,       // 100 (preço como % do par, padrão Treasury)
    int paymentFrequency        // 2 = semi-anual (todos os Treasuries)
) {
    /** Factory conveniente para Treasury com valores fixos de mercado. */
    public static Bond treasury(String cusip, String description,
                                BigDecimal couponRate, LocalDate maturityDate,
                                LocalDate issueDate) {
        return new Bond(cusip, description, couponRate, maturityDate,
                        issueDate, BigDecimal.valueOf(100), 2);
    }
}
