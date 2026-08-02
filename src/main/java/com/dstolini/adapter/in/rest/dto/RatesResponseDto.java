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
                ? String.format("Dados de %s. Atualização do dia ainda não disponível — exibindo último cache válido.", dataDate)
                : "Nenhum dado disponível. Aguardando primeira ingestão.";
        return new RatesResponseDto(rates, dataDate, true, msg);
    }
}
