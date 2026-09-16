package com.dstolini.domain.port.out;

import com.dstolini.domain.model.TreasuryRate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Porta de saída: persistência de taxas do Tesouro (cache local em PostgreSQL). */
public interface TreasuryRateRepository {
    void saveAll(List<TreasuryRate> rates);
    List<TreasuryRate> findLatest();
    Optional<TreasuryRate> findByCusip(String cusip);
    List<TreasuryRate> findByDate(LocalDate date);
}
