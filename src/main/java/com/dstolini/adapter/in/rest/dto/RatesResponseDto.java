package com.dstolini.adapter.in.rest.dto;

import com.dstolini.domain.model.TreasuryRate;

import java.time.LocalDate;
import java.util.List;

public record RatesResponseDto(
    List<TreasuryRate> rates,
    LocalDate dataDate,
    boolean isStale,
    String warning
) {
    public static RatesResponseDto fresh(List<TreasuryRate> rates, LocalDate dataDate) {
        return new RatesResponseDto(rates, dataDate, false, null);
    }

    public static RatesResponseDto stale(List<TreasuryRate> rates, LocalDate dataDate) {
        String msg = dataDate != null
                ? String.format("As informações exibidas são de %s. Os dados de hoje estão sendo obtidos e estarão disponíveis em breve.", dataDate)
                : "As informações ainda não estão disponíveis. Por favor, tente novamente em alguns minutos.";
        return new RatesResponseDto(rates, dataDate, true, msg);
    }
}
