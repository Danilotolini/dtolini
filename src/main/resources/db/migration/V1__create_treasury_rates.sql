CREATE TABLE treasury_rates
(
    id            BIGSERIAL    PRIMARY KEY,
    cusip         VARCHAR(9)   NOT NULL,
    security_type VARCHAR(100),
    description   VARCHAR(200),
    maturity_date DATE         NOT NULL,
    coupon_rate   DECIMAL(10, 6),
    yield         DECIMAL(10, 6),
    rate_date     DATE         NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_treasury_rates_cusip_date UNIQUE (cusip, rate_date)
);

CREATE INDEX idx_treasury_rates_rate_date ON treasury_rates (rate_date DESC);
CREATE INDEX idx_treasury_rates_cusip     ON treasury_rates (cusip);
CREATE INDEX idx_treasury_rates_maturity  ON treasury_rates (maturity_date);
