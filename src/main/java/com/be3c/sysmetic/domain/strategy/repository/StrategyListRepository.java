package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyListRepository extends JpaRepository<Strategy, Long> {

    // 공개중인 전략을 수익률 내림차순으로 페이징 조회
    Page<Strategy> findAllByStatusCode(String statusCode, Pageable pageable);

    // 특정 statusCode에 따른 전체 전략 수 조회
    Long countByStatusCode(String statusCode);

    // 닉네임으로 트레이더 조회, 일치한 닉네임, 전략 수 내림차순 정렬
    @Query("SELECT DISTINCT s FROM Strategy s JOIN FETCH s.trader m " +
            "WHERE m.nickname LIKE CONCAT('%', :nickname, '%') " +
            "AND m.roleCode = 'trader' " +
            "ORDER BY m.totalStrategyCount DESC")
    Page<Strategy> findDistinctByTraderNicknameContaining(@Param("nickname") String nickname, Pageable pageable);

    // 닉네임으로 트레이더 조회, 트레이더 별 전략 목록
    Page<Strategy> findByTrader(Member trader, Pageable pageable);

    // 트레이더가 등록한 전략수
    Long countByTraderId(Long traderId);

    default Pageable getPageable(Integer pageNum, String property) {
        int pageSize = 10;
        return PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc(property)));
    }
}