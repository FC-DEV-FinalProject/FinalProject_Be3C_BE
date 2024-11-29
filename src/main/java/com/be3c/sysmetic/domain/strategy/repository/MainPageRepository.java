package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainPageRepository extends JpaRepository<Strategy, Long> {

    // 트레이더 랭킹
    @Query("SELECT s From Strategy s WHERE s.statusCode = 'PUBLIC' ORDER BY s.followerCount DESC")
    Page<Strategy> findTop3ByFollowerCount(Pageable pageable);

    // 총 전략 수
    long count();

    // 대표전략 평균 지표 - 공개 전략만 유효한 데이터로 간주 
    // List<Strategy> findAllByStatusCode(String statusCode);

    // SM Score Top 5개
    @Query("SELECT s FROM Strategy s ORDER BY s.smScore DESC")
    Page<Strategy> findTop5SmScore(Pageable pageable);
}
