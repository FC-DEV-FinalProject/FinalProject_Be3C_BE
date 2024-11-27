package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StrategyDetailRepository extends JpaRepository<Strategy, Long> {

    // id와 statusCode로 Strategy 조회
    Optional<Strategy> findByIdAndStatusCode(Long id, String statusCode);
}