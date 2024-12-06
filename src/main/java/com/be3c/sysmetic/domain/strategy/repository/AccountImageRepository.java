package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.AccountImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountImageRepository extends JpaRepository<AccountImage, Long> {
    Page<AccountImage> findAllByStrategyIdOrderByAccountImageCreatedAt(Long strategyId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM AccountImage a WHERE a.strategy.id = :strategyId")
    int deleteByStrategyId(Long strategyId);
}
