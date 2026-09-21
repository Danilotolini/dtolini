package com.dtolini.adapter.out.treasury.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Resposta da Treasury Fiscal Data API.
 * Documentação: https://fiscaldata.treasury.gov/api-documentation/
 */
public record TreasuryApiResponse(
    List<SecurityData> data,
    Meta meta
) {
    public record SecurityData(
        @JsonProperty("cusip")          String cusip,
        @JsonProperty("security_type")  String securityType,
        @JsonProperty("security_desc")  String securityDesc,
        @JsonProperty("maturity_date")  String maturityDate,
        @JsonProperty("interest_rate")  String interestRate,
        @JsonProperty("yield_pct")      String yieldPct,
        @JsonProperty("issue_date")     String issueDate,
        @JsonProperty("record_date")    String recordDate
    ) {}

    public record Meta(
        @JsonProperty("count")  int count,
        @JsonProperty("total")  int total
    ) {}
}
