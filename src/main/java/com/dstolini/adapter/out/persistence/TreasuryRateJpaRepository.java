package com.dstolini.adapter.out.persistence;

import com.dstolini.adapter.out.persistence.entity.TreasuryRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

interface TreasuryRateJpaRepository extends JpaRepository<TreasuryRateEntity, Long> {

    @Query("SELECT r FROM TreasuryRateEntity r " +
           "WHERE r.rateDate = (SELECT MAX(r2.rateDate) FROM TreasuryRateEntity r2) " +
           "ORDER BY r.maturityDate")
    List<TreasuryRateEntity> findAllByLatestDate();

    Optional<TreasuryRateEntity> findTopByCusipOrderByRateDateDesc(String cusip);

    List<TreasuryRateEntity> findByRateDateOrderByMaturityDate(LocalDate rateDate);
}
