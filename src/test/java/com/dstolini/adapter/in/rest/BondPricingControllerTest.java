package com.dstolini.adapter.in.rest;

import com.dstolini.domain.model.PricingResult;
import com.dstolini.domain.port.in.BondPricingUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * CAMADA: controller REST. Usa @WebMvcTest — carrega só a camada web.
 *
 * O que BondPricingController promete:
 *   1. Request válida com YTM → 200 com todos os campos de precificação
 *   2. Request válida com preço → 200
 *   3. cleanPrice fora do range → 400 com código VALIDATION_ERROR
 *   4. PricingEngine ainda não implementado → 501 NOT_IMPLEMENTED (não 500)
 *
 * O test de contrato (4) é importante: enquanto PricingEngine está como stub,
 * o cliente recebe 501 em vez de 500 genérico — sabe que é intencional.
 */
@WebMvcTest(BondPricingController.class)
class BondPricingControllerTest {

    @Autowired MockMvc mvc;
    @MockBean BondPricingUseCase pricingUseCase;

    private static final String VALID_YTM_BODY = """
            {
                "cusip": "912810RV0",
                "couponRate": 0.045,
                "maturityDate": "2029-08-31",
                "issueDate": "2024-08-31",
                "cleanPrice": 98.75,
                "settlementDate": "2024-09-03"
            }
            """;

    private static final String VALID_PRICE_BODY = """
            {
                "cusip": "912810RV0",
                "couponRate": 0.045,
                "maturityDate": "2029-08-31",
                "issueDate": "2024-08-31",
                "yieldToMaturity": 0.0512,
                "settlementDate": "2024-09-03"
            }
            """;

    @Test
    void ytm_endpoint_returns_200_with_all_pricing_fields() throws Exception {
        when(pricingUseCase.calculateYtm(any(), any(), any())).thenReturn(aPricingResult());

        mvc.perform(post("/api/v1/pricing/ytm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_YTM_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yieldToMaturity").exists())
                .andExpect(jsonPath("$.cleanPrice").exists())
                .andExpect(jsonPath("$.dirtyPrice").exists())
                .andExpect(jsonPath("$.accruedInterest").exists());
    }

    @Test
    void price_endpoint_returns_200_with_all_pricing_fields() throws Exception {
        when(pricingUseCase.calculatePrice(any(), any(), any())).thenReturn(aPricingResult());

        mvc.perform(post("/api/v1/pricing/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRICE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cleanPrice").exists());
    }

    @Test
    void cleanPrice_zero_returns_400_validation_error() throws Exception {
        String invalidBody = VALID_YTM_BODY.replace("98.75", "0.00");

        mvc.perform(post("/api/v1/pricing/ytm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void cleanPrice_above_200_returns_400_validation_error() throws Exception {
        String invalidBody = VALID_YTM_BODY.replace("98.75", "201.00");

        mvc.perform(post("/api/v1/pricing/ytm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void unimplemented_engine_returns_501_not_implemented() throws Exception {
        when(pricingUseCase.calculateYtm(any(), any(), any()))
                .thenThrow(new UnsupportedOperationException("PricingEngine nao implementado"));

        mvc.perform(post("/api/v1/pricing/ytm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_YTM_BODY))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.code").value("NOT_IMPLEMENTED"));
    }

    private static PricingResult aPricingResult() {
        return new PricingResult(
                new BigDecimal("0.0512"), new BigDecimal("98.75"),
                new BigDecimal("99.12"), new BigDecimal("0.37"),
                LocalDate.of(2024, 9, 3));
    }
}
