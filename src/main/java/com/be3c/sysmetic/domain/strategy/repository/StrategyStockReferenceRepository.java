package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.StrategyStockReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface StrategyStockReferenceRepository extends JpaRepository<StrategyStockReference, Long> {
    @Query("""
        SELECT sr.stock.id FROM StrategyStockReference sr 
        WHERE sr.strategy.id = :strategyId
    """)
    List<Long> findStockIdsByStrategyId(Long strategyId);

    void deleteByStrategyId(Long strategyId);

    @Modifying
    @Query("""
        DELETE FROM StrategyStockReference sr 
        WHERE sr.strategy.id = :strategyId 
        AND sr.stock.id IN :stockIds
    """)
    void deleteByStrategyIdAndStockIds(Long strategyId, Set<Long> stockIds);
}