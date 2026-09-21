package com.dtolini.adapter.out.treasury;

import com.dtolini.domain.model.TreasuryRate;
import com.dtolini.domain.port.out.MarketDataSource;
import com.dtolini.domain.port.out.TreasuryRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Job de ingestão assíncrona de taxas do Tesouro americano (ADR-001).
 *
 * Se a primeira tentativa falhar ou retornar vazio, reagenda automaticamente
 * até MAX_RETRIES vezes com intervalo de RETRY_INTERVAL entre cada tentativa.
 * Em caso de falha total, o cache local é preservado e o usuário recebe aviso.
 */
@Component
class TreasuryIngestJob {

    private static final Logger log = LoggerFactory.getLogger(TreasuryIngestJob.class);

    static final int MAX_RETRIES = 3;
    static final Duration RETRY_INTERVAL = Duration.ofMinutes(15);

    private final MarketDataSource marketDataSource;
    private final TreasuryRateRepository rateRepository;
    private final ThreadPoolTaskScheduler taskScheduler;

    TreasuryIngestJob(MarketDataSource marketDataSource,
                      TreasuryRateRepository rateRepository,
                      ThreadPoolTaskScheduler taskScheduler) {
        this.marketDataSource = marketDataSource;
        this.rateRepository = rateRepository;
        this.taskScheduler = taskScheduler;
    }

    // 21:30 UTC = 18:30 BRT = 17:30 ET — após publicação do H.15 (16:15 ET)
    @Scheduled(cron = "${treasury.ingest.cron:0 30 21 * * MON-FRI}", zone = "UTC")
    void ingest() {
        runWithRetry(1);
    }

    private void runWithRetry(int attempt) {
        log.info("Ingestão iniciada (tentativa {}/{})", attempt, MAX_RETRIES);
        try {
            List<TreasuryRate> rates = marketDataSource.fetchLatestRates();

            if (rates.isEmpty()) {
                scheduleRetryOrGiveUp(attempt, "Treasury API retornou vazio");
                return;
            }

            rateRepository.saveAll(rates);
            log.info("Ingestão concluída: {} taxas armazenadas (tentativa {})", rates.size(), attempt);

        } catch (Exception e) {
            scheduleRetryOrGiveUp(attempt, e.getMessage());
        }
    }

    private void scheduleRetryOrGiveUp(int attempt, String reason) {
        if (attempt >= MAX_RETRIES) {
            log.error("Ingestão falhou após {} tentativas ({}). Dados anteriores preservados no cache.",
                    MAX_RETRIES, reason);
            return;
        }

        int next = attempt + 1;
        Instant nextRun = Instant.now().plus(RETRY_INTERVAL);
        log.warn("Tentativa {} falhou ({}). Reagendando tentativa {}/{} em {} min.",
                attempt, reason, next, MAX_RETRIES, RETRY_INTERVAL.toMinutes());

        taskScheduler.schedule(() -> runWithRetry(next), nextRun);
    }
}
