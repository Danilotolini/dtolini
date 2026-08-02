package com.dstolini.domain.port.out;

import com.dstolini.domain.model.TreasuryRate;

import java.util.List;

/** Porta de saída: busca taxas de mercado de uma fonte externa (Treasury Fiscal Data API). */
public interface MarketDataSource {
    List<TreasuryRate> fetchLatestRates();
}
