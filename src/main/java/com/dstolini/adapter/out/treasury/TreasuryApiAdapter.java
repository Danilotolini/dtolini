package com.dstolini.adapter.out.treasury;

import com.dstolini.adapter.out.treasury.dto.TreasuryApiResponse;
import com.dstolini.domain.model.TreasuryRate;
import com.dstolini.domain.port.out.MarketDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Busca taxas de Treasuries na Treasury Fiscal Data API.
 *
 * Endpoint de referência:
 *   https://api.fiscaldata.treasury.gov/services/api/v1/accounting/od/avg_interest_rates
 *
 * Verifique os nomes exatos dos campos em:
 *   https://fiscaldata.treasury.gov/datasets/average-interest-rates-treasury-securities/
 */
@Component
class TreasuryApiAdapter implements MarketDataSource {

    private static final Logger log = LoggerFactory.getLogger(TreasuryApiAdapter.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String ENDPOINT =
            "/services/api/v1/accounting/od/avg_interest_rates" +
            "?fields=cusip,security_type,security_desc,maturity_date,interest_rate,yield_pct,issue_date,record_date" +
            "&sort=-record_date" +
            "&page[size]=250";

    private final RestClient restClient;

    TreasuryApiAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<TreasuryRate> fetchLatestRates() {
        try {
            String filter = "&filter=record_date:gte:" + LocalDate.now().minusDays(3);
            TreasuryApiResponse response = restClient.get()
                    .uri(ENDPOINT + filter)
                    .retrieve()
                    .body(TreasuryApiResponse.class);

            if (response == null || response.data() == null || response.data().isEmpty()) {
                log.warn("Treasury API retornou resposta vazia");
                return Collections.emptyList();
            }

            List<TreasuryRate> rates = response.data().stream()
                    .map(this::toDomain)
                    .filter(r -> r.yield() != null && r.rateDate() != null)
                    .toList();

            log.info("Treasury API: {} taxas recebidas", rates.size());
            return rates;

        } catch (Exception e) {
            log.error("Falha ao buscar taxas da Treasury API: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private TreasuryRate toDomain(TreasuryApiResponse.SecurityData d) {
        return new TreasuryRate(
                d.cusip(),
                d.securityType(),
                d.securityDesc(),
                parseDate(d.maturityDate()),
                parseRate(d.interestRate()),
                parseRate(d.yieldPct()),
                parseDate(d.recordDate()));
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.length() > 10 ? s.substring(0, 10) : s, DATE_FMT); }
        catch (Exception e) { return null; }
    }

    private BigDecimal parseRate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return new BigDecimal(s.trim()).divide(BigDecimal.valueOf(100)); }
        catch (NumberFormatException e) { return null; }
    }
}
