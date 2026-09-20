package com.dtolini.adapter.in.rest;

import com.dtolini.adapter.in.rest.dto.PriceRequestDto;
import com.dtolini.adapter.in.rest.dto.PricingResponseDto;
import com.dtolini.adapter.in.rest.dto.YtmRequestDto;
import com.dtolini.domain.model.Bond;
import com.dtolini.domain.model.PricingResult;
import com.dtolini.domain.port.in.BondPricingUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pricing")
class BondPricingController {

    private final BondPricingUseCase pricingUseCase;

    BondPricingController(BondPricingUseCase pricingUseCase) {
        this.pricingUseCase = pricingUseCase;
    }

    /**
     * Calcula o Yield to Maturity dado o preço limpo de mercado.
     *
     * POST /api/v1/pricing/ytm
     */
    @PostMapping("/ytm")
    ResponseEntity<PricingResponseDto> calculateYtm(@Valid @RequestBody YtmRequestDto req) {
        Bond bond = Bond.treasury(req.cusip(), null, req.couponRate(),
                                  req.maturityDate(), req.issueDate());
        PricingResult result = pricingUseCase.calculateYtm(bond, req.cleanPrice(), req.settlementDate());
        return ResponseEntity.ok(toResponse(result));
    }

    /**
     * Calcula o preço limpo e sujo dado o YTM.
     *
     * POST /api/v1/pricing/price
     */
    @PostMapping("/price")
    ResponseEntity<PricingResponseDto> calculatePrice(@Valid @RequestBody PriceRequestDto req) {
        Bond bond = Bond.treasury(req.cusip(), null, req.couponRate(),
                                  req.maturityDate(), req.issueDate());
        PricingResult result = pricingUseCase.calculatePrice(bond, req.yieldToMaturity(), req.settlementDate());
        return ResponseEntity.ok(toResponse(result));
    }

    private PricingResponseDto toResponse(PricingResult r) {
        return new PricingResponseDto(
                r.yieldToMaturity(), r.cleanPrice(), r.dirtyPrice(),
                r.accruedInterest(), r.settlementDate());
    }
}
