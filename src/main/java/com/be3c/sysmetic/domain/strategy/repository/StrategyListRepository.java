package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyListRepository extends JpaRepository<Strategy, Long> {
    // 공개중인 전략을 수익률 내림차순으로 페이징 조회
    // JPA가 쿼리 생성
    Page<Strategy> findAllByStatusCode(String statusCode, Pageable pageable);

    // 특정 statusCode에 따른 전체 전략 수 조회
    Long countByStatusCode(String statusCode);

    default Pageable getPageable(Integer pageNum) {
        int pageSize = 10;
        return PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("accumProfitRate")));
    }
}