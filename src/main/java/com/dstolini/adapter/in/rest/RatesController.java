package com.dstolini.adapter.in.rest;

import com.dstolini.domain.exception.RateNotFoundException;
import com.dstolini.domain.model.TreasuryRate;
import com.dstolini.domain.port.out.TreasuryRateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rates")
class RatesController {

    private final TreasuryRateRepository rateRepository;

    RatesController(TreasuryRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    /** GET /api/v1/rates — retorna as taxas mais recentes do cache local. */
    @GetMapping
    ResponseEntity<List<TreasuryRate>> getLatest() {
        return ResponseEntity.ok(rateRepository.findLatest());
    }

    /** GET /api/v1/rates/{cusip} — retorna a taxa mais recente de um título específico. */
    @GetMapping("/{cusip}")
    ResponseEntity<TreasuryRate> getByCusip(@PathVariable String cusip) {
        return rateRepository.findByCusip(cusip)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RateNotFoundException(cusip));
    }
}
