package com.dstolini.adapter.in.rest;

import com.dstolini.adapter.in.rest.dto.RatesResponseDto;
import com.dstolini.domain.exception.RateNotFoundException;
import com.dstolini.domain.model.TreasuryRate;
import com.dstolini.domain.port.out.TreasuryRateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rates")
class RatesController {

    private final TreasuryRateRepository rateRepository;

    RatesController(TreasuryRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    /**
     * GET /api/v1/rates
     *
     * Retorna as taxas mais recentes do cache local.
     * Quando os dados são de um dia útil anterior, inclui {@code isStale: true}
     * e {@code warning} com mensagem explicativa — a atualização ocorre automaticamente
     * em background sem derrubar a resposta.
     */
    @GetMapping
    ResponseEntity<RatesResponseDto> getLatest() {
        List<TreasuryRate> rates = rateRepository.findLatest();

        LocalDate dataDate = rates.isEmpty() ? null : rates.getFirst().rateDate();
        boolean stale = isStale(dataDate);

        RatesResponseDto response = stale
                ? RatesResponseDto.stale(rates, dataDate)
                : RatesResponseDto.fresh(rates, dataDate);

        return ResponseEntity.ok(response);
    }

    /** GET /api/v1/rates/{cusip} */
    @GetMapping("/{cusip}")
    ResponseEntity<TreasuryRate> getByCusip(@PathVariable String cusip) {
        return rateRepository.findByCusip(cusip)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RateNotFoundException(cusip));
    }

    /**
     * Dados são considerados desatualizados quando o rateDate é anterior ao
     * último dia útil — isso cobre fins de semana corretamente sem forçar
     * a Treasury a publicar aos sábados e domingos.
     */
    private boolean isStale(LocalDate dataDate) {
        if (dataDate == null) return true;
        return dataDate.isBefore(lastBusinessDay());
    }

    private LocalDate lastBusinessDay() {
        LocalDate today = LocalDate.now();
        return switch (today.getDayOfWeek()) {
            case SATURDAY -> today.minusDays(1);  // sexta
            case SUNDAY   -> today.minusDays(2);  // sexta
            default       -> today;
        };
    }
}
