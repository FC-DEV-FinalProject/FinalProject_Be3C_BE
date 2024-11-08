package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StrategyListRepository extends JpaRepository<Strategy, Long> {
    // 팔로우 랭킹 조회 - Top 3
    // Optional<Strategy> findTop3ByFollowerCount();

    // 총 트레이더 수
    // Long findCountAllTrader();

    // 전략 목록 페이징 - 공개중인 전략을 수익률 내림차순으로 정렬
    // JPA가 쿼리 생성
    Page<Strategy> findAllByStatusCode(String statusCode, Pageable pageable);


    default Pageable getPageable(Integer pageNum) {
        int pageSize = 10;
        return PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("accumProfitRate")));
    }

    // 총 페이지 수 (공개중인 것만)
    default int getTotalPage(String statusCode) {
        int pageSize = 10;
        long totalStrategyCount = countByStatusCode(statusCode);
        return (int) Math.ceil((double) totalStrategyCount / pageSize);
    }

    // 총 전략 수 - 공개중인 전략만
    Long countByStatusCode(String statusCode);
}