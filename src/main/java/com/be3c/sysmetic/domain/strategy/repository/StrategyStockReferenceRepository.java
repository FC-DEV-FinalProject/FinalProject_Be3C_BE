package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.StrategyStockReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyStockReferenceRepository extends JpaRepository<StrategyStockReference, Long> {
    List<StrategyStockReference> findByStrategyId(Long strategyId);
}