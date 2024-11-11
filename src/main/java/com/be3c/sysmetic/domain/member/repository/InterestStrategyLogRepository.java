package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.InterestStrategyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestStrategyLogRepository extends JpaRepository<InterestStrategyLog, Long> {

}
