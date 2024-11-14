package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.strategy.dto.TraderNicknameListDto;
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


    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.TraderNicknameListDto(" +
            "t.id, t.nickname, t.roleCode, t.totalFollow, " +
            "COUNT(CASE WHEN s.statusCode = 'ST001' THEN 1 END)) " +
            "FROM Member t JOIN Strategy s ON s.trader.id = t.id " +
            "WHERE t.nickname LIKE concat('%', :nickname, '%') AND t.roleCode = 'trader' " +
            "GROUP BY t.id, t.nickname, t.roleCode, t.totalFollow " +
            "ORDER BY COUNT(CASE WHEN s.statusCode ='ST001' THEN 1 END) DESC")
    Page<TraderNicknameListDto> findDistinctByTraderNickname(@Param("nickname") String nickname, Pageable pageable);


    // 닉네임으로 트레이더 조회 -> 트레이더 별 전략 목록
    Page<Strategy> findAllByTraderAndStatusCode(Member trader, String statusCode, Pageable pageable);


    // Pageable 반환 메서드
    default Pageable getPageable(Integer pageNum, String property) {
        int pageSize = 10;
        return PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc(property)));
    }
}