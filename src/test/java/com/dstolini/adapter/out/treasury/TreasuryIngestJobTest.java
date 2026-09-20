package com.dstolini.adapter.out.treasury;

import com.dstolini.domain.model.TreasuryRate;
import com.dstolini.domain.port.out.MarketDataSource;
import com.dstolini.domain.port.out.TreasuryRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * CAMADA: adaptador de saída (ingestão). Usa Mockito para isolar o scheduler.
 *
 * O que TreasuryIngestJob promete:
 *   1. Sucesso no primeiro intento → salva os dados, não agenda retry
 *   2. Resposta vazia → agenda retry sem salvar
 *   3. Exceção da API → agenda retry
 *   4. Após MAX_RETRIES falhas → para de reagendar (não fica em loop eterno)
 *
 * O retry usa o ThreadPoolTaskScheduler para agendar uma lambda com o próximo intento.
 * Para testar a cadeia completa, capturamos e executamos essas lambdas manualmente.
 * É assim que o ingest se comporta em produção — sem diferença.
 */
@ExtendWith(MockitoExtension.class)
class TreasuryIngestJobTest {

    @Mock MarketDataSource     marketDataSource;
    @Mock TreasuryRateRepository rateRepository;
    @Mock ThreadPoolTaskScheduler taskScheduler;

    TreasuryIngestJob job;

    @BeforeEach
    void setUp() {
        job = new TreasuryIngestJob(marketDataSource, rateRepository, taskScheduler);
    }

    @Test
    void successful_ingest_saves_rates_and_never_schedules_retry() {
        List<TreasuryRate> rates = List.of(aRate());
        when(marketDataSource.fetchLatestRates()).thenReturn(rates);

        job.ingest();

        verify(rateRepository).saveAll(rates);
        verifyNoInteractions(taskScheduler);
    }

    @Test
    void empty_api_response_schedules_retry_without_saving() {
        when(marketDataSource.fetchLatestRates()).thenReturn(List.of());

        job.ingest();

        verify(taskScheduler).schedule(any(Runnable.class), any(Instant.class));
        verify(rateRepository, never()).saveAll(any());
    }

    @Test
    void api_exception_schedules_retry() {
        when(marketDataSource.fetchLatestRates()).thenThrow(new RuntimeException("timeout"));

        job.ingest();

        verify(taskScheduler).schedule(any(Runnable.class), any(Instant.class));
        verify(rateRepository, never()).saveAll(any());
    }

    @Test
    void after_max_retries_no_more_scheduling() {
        /*
         * Simula a cadeia completa de retries executando os runnables agendados em sequência.
         * doAnswer coleta cada runnable no momento em que é agendado, evitando o problema
         * de re-captura do ArgumentCaptor em verify(times(n)).
         *
         * Esperado: taskScheduler.schedule chamado exatamente MAX_RETRIES - 1 vezes.
         */
        when(marketDataSource.fetchLatestRates()).thenReturn(List.of());

        List<Runnable> scheduled = new ArrayList<>();
        doAnswer(inv -> { scheduled.add(inv.getArgument(0)); return null; })
                .when(taskScheduler).schedule(any(Runnable.class), any(Instant.class));

        job.ingest();           // tentativa 1 → schedula tentativa 2
        scheduled.get(0).run(); // tentativa 2 → schedula tentativa 3
        scheduled.get(1).run(); // tentativa 3 = MAX_RETRIES → para

        assertThat(scheduled).hasSize(TreasuryIngestJob.MAX_RETRIES - 1);
    }

    @Test
    void retry_is_scheduled_with_positive_delay_in_the_future() {
        when(marketDataSource.fetchLatestRates()).thenReturn(List.of());
        Instant before = Instant.now();

        job.ingest();

        ArgumentCaptor<Instant> timeCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(taskScheduler).schedule(any(), timeCaptor.capture());

        assertThat(timeCaptor.getValue()).isAfter(before);
    }

    private static TreasuryRate aRate() {
        return new TreasuryRate("912810RV0", "Note", "5Y Note",
                LocalDate.of(2029, 8, 31), new BigDecimal("0.045"),
                new BigDecimal("0.0512"), LocalDate.now());
    }
}
