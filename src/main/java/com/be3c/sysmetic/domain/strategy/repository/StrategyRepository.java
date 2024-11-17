package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {
    boolean existsByName(String name); // 전략명 중복 확인

    @Query("SELECT s FROM Strategy s WHERE s.statusCode = 'PUBLIC'")
    List<Strategy> findAllByPublicStatus();
}
