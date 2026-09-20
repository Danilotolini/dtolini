package com.dstolini.adapter.in.rest;

import com.dstolini.domain.model.TreasuryRate;
import com.dstolini.domain.port.out.TreasuryRateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * CAMADA: controller REST. Usa @WebMvcTest — carrega só a camada web (sem DB, sem ingest).
 *
 * O que RatesController promete:
 *   1. Dados ausentes (cache vazio) → 200 com isStale=true e warning preenchido
 *   2. Dados antigos (mais de 1 dia útil atrás) → 200 com isStale=true
 *   3. Dados de hoje → 200 com isStale=false
 *   4. CUSIP desconhecido → 404 com código de erro estruturado
 *   5. CUSIP encontrado → 200 com o objeto TreasuryRate
 *
 * Nota: isStale é calculado em relação a LocalDate.now(), então:
 *   - today é sempre "fresh" (hoje nunca é antes de hoje)
 *   - minusDays(5) é sempre "stale" (5 dias atrás sempre precede o último dia útil)
 */
@WebMvcTest(RatesController.class)
class RatesControllerTest {

    @Autowired MockMvc mvc;
    @MockBean TreasuryRateRepository rateRepository;

    @Test
    void empty_cache_returns_stale_with_warning() throws Exception {
        when(rateRepository.findLatest()).thenReturn(List.of());

        mvc.perform(get("/api/v1/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isStale").value(true))
                .andExpect(jsonPath("$.warning").isNotEmpty());
    }

    @Test
    void old_data_returns_stale_flag() throws Exception {
        when(rateRepository.findLatest()).thenReturn(List.of(aRate(LocalDate.now().minusDays(5))));

        mvc.perform(get("/api/v1/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isStale").value(true))
                .andExpect(jsonPath("$.rates").isArray());
    }

    @Test
    void todays_data_returns_fresh() throws Exception {
        when(rateRepository.findLatest()).thenReturn(List.of(aRate(LocalDate.now())));

        mvc.perform(get("/api/v1/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isStale").value(false));
    }

    @Test
    void unknown_cusip_returns_404_with_error_code() throws Exception {
        when(rateRepository.findByCusip("UNKNOWN")).thenReturn(Optional.empty());

        mvc.perform(get("/api/v1/rates/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RATE_NOT_FOUND"));
    }

    @Test
    void known_cusip_returns_the_rate() throws Exception {
        TreasuryRate rate = aRate(LocalDate.now());
        when(rateRepository.findByCusip("912810RV0")).thenReturn(Optional.of(rate));

        mvc.perform(get("/api/v1/rates/912810RV0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cusip").value("912810RV0"));
    }

    private static TreasuryRate aRate(LocalDate rateDate) {
        return new TreasuryRate("912810RV0", "Note", "5Y Note",
                LocalDate.of(2029, 8, 31), new BigDecimal("0.045"),
                new BigDecimal("0.0512"), rateDate);
    }
}
