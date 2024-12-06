package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StrategyDetailRepository extends JpaRepository<Strategy, Long> {

    @Query("SELECT s FROM Strategy s WHERE s.id = :id")
    Optional<Strategy> findPublicStrategy(@Param("id") Long id);
}