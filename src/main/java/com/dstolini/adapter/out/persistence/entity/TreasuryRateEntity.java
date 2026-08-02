package com.dstolini.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "treasury_rates",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_treasury_rates_cusip_date",
        columnNames = {"cusip", "rate_date"}
    )
)
public class TreasuryRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 9)
    private String cusip;

    @Column(name = "security_type", length = 100)
    private String securityType;

    @Column(length = 200)
    private String description;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Column(name = "coupon_rate", precision = 10, scale = 6)
    private BigDecimal couponRate;

    @Column(precision = 10, scale = 6)
    private BigDecimal yield;

    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected TreasuryRateEntity() {}

    public TreasuryRateEntity(String cusip, String securityType, String description,
                               LocalDate maturityDate, BigDecimal couponRate,
                               BigDecimal yield, LocalDate rateDate) {
        this.cusip = cusip;
        this.securityType = securityType;
        this.description = description;
        this.maturityDate = maturityDate;
        this.couponRate = couponRate;
        this.yield = yield;
        this.rateDate = rateDate;
    }

    public Long getId()                { return id; }
    public String getCusip()           { return cusip; }
    public String getSecurityType()    { return securityType; }
    public String getDescription()     { return description; }
    public LocalDate getMaturityDate() { return maturityDate; }
    public BigDecimal getCouponRate()  { return couponRate; }
    public BigDecimal getYield()       { return yield; }
    public LocalDate getRateDate()     { return rateDate; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
}
