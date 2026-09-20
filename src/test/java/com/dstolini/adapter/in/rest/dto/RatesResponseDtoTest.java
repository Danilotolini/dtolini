package com.dstolini.adapter.in.rest.dto;

import com.dstolini.domain.model.TreasuryRate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * CAMADA: DTO de saída. Zero Spring.
 *
 * RatesResponseDto carrega a promessa feita ao cliente: quando os dados são
 * atuais, isStale=false e warning=null. Quando estão desatualizados, isStale=true
 * e a mensagem explica o contexto sem expor detalhes internos.
 *
 * Testar os factory methods aqui significa que qualquer regressão na mensagem
 * ou no campo isStale é detectada antes de chegar ao cliente.
 */
class RatesResponseDtoTest {

    private static final LocalDate DATE = LocalDate.of(2024, 9, 1);

    private static TreasuryRate aRate(LocalDate rateDate) {
        return new TreasuryRate("912810RV0", "Note", "5Y Note",
                LocalDate.of(2029, 8, 31), new BigDecimal("0.045"),
                new BigDecimal("0.0512"), rateDate);
    }

    @Test
    void fresh_marks_isStale_false_and_warning_null() {
        RatesResponseDto dto = RatesResponseDto.fresh(List.of(aRate(DATE)), DATE);

        assertThat(dto.isStale()).isFalse();
        assertThat(dto.warning()).isNull();
        assertThat(dto.dataDate()).isEqualTo(DATE);
    }

    @Test
    void stale_with_date_includes_date_in_warning_message() {
        RatesResponseDto dto = RatesResponseDto.stale(List.of(aRate(DATE)), DATE);

        assertThat(dto.isStale()).isTrue();
        assertThat(dto.warning()).contains("2024-09-01");
    }

    @Test
    void stale_with_null_date_shows_generic_unavailable_message() {
        RatesResponseDto dto = RatesResponseDto.stale(List.of(), null);

        assertThat(dto.isStale()).isTrue();
        assertThat(dto.warning()).isNotBlank();
        assertThat(dto.dataDate()).isNull();
    }

    @Test
    void stale_preserves_rate_list() {
        List<TreasuryRate> rates = List.of(aRate(DATE));
        RatesResponseDto dto = RatesResponseDto.stale(rates, DATE);

        assertThat(dto.rates()).hasSize(1);
        assertThat(dto.rates().getFirst().cusip()).isEqualTo("912810RV0");
    }
}
