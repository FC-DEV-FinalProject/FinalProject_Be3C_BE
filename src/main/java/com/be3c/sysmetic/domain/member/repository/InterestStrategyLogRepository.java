package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.InterestStrategyLog;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestStrategyLogRepository extends JpaRepository<InterestStrategyLog, Long> {
    @Modifying
    @Query("DELETE FROM InterestStrategyLog l WHERE l.interestStrategy.id = :interestStrategyId")
    void deleteByInterestStrategyId(@Param("interestStrategyId") Long interestStrategyId);
}
