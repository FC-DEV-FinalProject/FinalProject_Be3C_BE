package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StrategyDetailRepository extends JpaRepository<Strategy, Long> {
    Optional<Strategy> findByIdAndStatusCode(Long id, String statusCode);
}