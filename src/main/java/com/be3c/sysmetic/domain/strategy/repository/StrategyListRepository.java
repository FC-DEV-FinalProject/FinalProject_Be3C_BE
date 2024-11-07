package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyListRepository extends JpaRepository<Strategy, Long>, PagingAndSortingRepository<Strategy, Long> {

    // @Query("SELECT s FROM Strategy s ORDER BY s.accumProfitRate DESC LIMIT 10")
    @Query("SELECT s FROM Strategy s ORDER BY s.accumProfitRate DESC")
    Page<Strategy> findByAccumProfitRate(Pageable pageable);
}