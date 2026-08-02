package com.dstolini.adapter.out.treasury;

import com.dstolini.domain.model.TreasuryRate;
import com.dstolini.domain.port.out.MarketDataSource;
import com.dstolini.domain.port.out.TreasuryRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Job de ingestão assíncrona de taxas do Tesouro americano (ADR-001).
 *
 * Roda de segunda a sexta após o fechamento do mercado americano.
 * Em caso de falha na API externa, os dados locais permanecem intactos.
 */
@Component
class TreasuryIngestJob {

    private static final Logger log = LoggerFactory.getLogger(TreasuryIngestJob.class);

    private final MarketDataSource marketDataSource;
    private final TreasuryRateRepository rateRepository;

    TreasuryIngestJob(MarketDataSource marketDataSource, TreasuryRateRepository rateRepository) {
        this.marketDataSource = marketDataSource;
        this.rateRepository = rateRepository;
    }

    // 18h30 BRT (21h30 UTC) — após o fechamento do mercado americano
    @Scheduled(cron = "${treasury.ingest.cron:0 30 21 * * MON-FRI}", zone = "UTC")
    void ingest() {
        log.info("Ingestão iniciada");
        try {
            List<TreasuryRate> rates = marketDataSource.fetchLatestRates();
            if (rates.isEmpty()) {
                log.warn("Nenhuma taxa retornada — cache local preservado");
                return;
            }
            rateRepository.saveAll(rates);
            log.info("Ingestão concluída: {} taxas armazenadas", rates.size());
        } catch (Exception e) {
            log.error("Falha na ingestão — dados locais preservados: {}", e.getMessage(), e);
        }
    }
}
