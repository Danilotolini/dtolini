package com.dstolini.adapter.out.persistence;

import com.dstolini.adapter.out.persistence.entity.TreasuryRateEntity;
import com.dstolini.domain.model.TreasuryRate;
import com.dstolini.domain.port.out.TreasuryRateRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
class TreasuryRateRepositoryAdapter implements TreasuryRateRepository {

    private final TreasuryRateJpaRepository jpa;

    TreasuryRateRepositoryAdapter(TreasuryRateJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    @Transactional
    public void saveAll(List<TreasuryRate> rates) {
        List<TreasuryRateEntity> entities = rates.stream().map(this::toEntity).toList();
        jpa.saveAll(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreasuryRate> findLatest() {
        return jpa.findAllByLatestDate().stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TreasuryRate> findByCusip(String cusip) {
        return jpa.findTopByCusipOrderByRateDateDesc(cusip).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreasuryRate> findByDate(LocalDate date) {
        return jpa.findByRateDateOrderByMaturityDate(date).stream().map(this::toDomain).toList();
    }

    private TreasuryRateEntity toEntity(TreasuryRate r) {
        return new TreasuryRateEntity(r.cusip(), r.securityType(), r.description(),
                r.maturityDate(), r.couponRate(), r.yield(), r.rateDate());
    }

    private TreasuryRate toDomain(TreasuryRateEntity e) {
        return new TreasuryRate(e.getCusip(), e.getSecurityType(), e.getDescription(),
                e.getMaturityDate(), e.getCouponRate(), e.getYield(), e.getRateDate());
    }
}
