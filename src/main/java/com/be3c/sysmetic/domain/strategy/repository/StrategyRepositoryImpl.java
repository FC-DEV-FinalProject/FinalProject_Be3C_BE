package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StrategyRepositoryImpl {

    // 임시
    private final EntityManager em;

    public Strategy findOne(Long id) {
        return em.find(Strategy.class, id);
    }
}
