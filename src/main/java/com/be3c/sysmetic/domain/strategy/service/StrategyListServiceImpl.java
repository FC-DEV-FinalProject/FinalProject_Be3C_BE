package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListByTraderDto;
import com.be3c.sysmetic.domain.strategy.dto.StrategyListDto;
import com.be3c.sysmetic.domain.strategy.dto.TraderListDto;
import com.be3c.sysmetic.domain.strategy.repository.StrategyListRepository;
import com.be3c.sysmetic.global.common.response.ApiResponse;
import com.be3c.sysmetic.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyListServiceImpl implements StrategyListService {

    private final StrategyListRepository strategyListRepository;
    private final MemberRepository memberRepository;

    /*
    getTotalPageNumber : 특정 statusCode에 따른 전체 페이지 수 계산
    */
    @Override
    public int getTotalPageNumber(String statusCode, int pageSize) {
        long totalStrategyCount = strategyListRepository.countByStatusCode(statusCode);
        return (int) Math.ceil((double) totalStrategyCount / pageSize);
    }

    /*
        findStrategyPage : 메인 전략 목록 페이지 (수익률순 조회)
        Strategy 엔티티를 StrategyListDto로 반환
    */
    @Override
    // public Page<StrategyListDto> findStrategyPage(Integer pageNum) {
    public PageResponse<StrategyListDto> findStrategyPage(Integer pageNum) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("accumProfitRate")));
        String statusCode = "ST001"; // 공개중인 전략

         Page<StrategyListDto> strategies = strategyListRepository.findAllByStatusCode(statusCode, pageable)
                .map(strategy -> new StrategyListDto(
                        strategy.getName(),
                        "stock name", // 종목 이름 가져오는 메서드 필요
                        strategy.getCycle(),
                        strategy.getTrader().getNickname(),
                        strategy.getAccumProfitRate(),
                        strategy.getMdd(),
                        strategy.getSmScore()
                ));


         // 페이지에 내용 있는지 검증
         // if (!strategyListPage.hasContent()) {
         //     return
         // }

         return PageResponse.<StrategyListDto>builder()
                 .currentPage(strategies.getNumber())
                 .pageSize(strategies.getSize())
                 .totalElement(strategies.getTotalElements())
                 .totalPages(strategies.getTotalPages())
                 .content(strategies.getContent())
                 .build();
    }


    /*
        findByTraderNickname : 트레이더 닉네임으로 검색, 일치한 닉네임, 팔로우 수 정렬
    */
    @Override
    public PageResponse<TraderListDto> findTraderNickname(String nickname) {

        // log.info("Searching for nickname in Service: {}", nickname); // 로그 추가

        int pageNum = 0, pageSize = 10;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("followerCount")));

        Page<TraderListDto> traders = strategyListRepository.findByTraderNicknameContaining(nickname, pageable)
                .map(strategy -> new TraderListDto(
                        strategy.getTrader().getId(),
                        strategy.getTrader().getNickname(),
                        strategy.getFollowerCount(),
                        // 총 검색 결과 수
                        strategyListRepository.countByTraderNicknameContaining(nickname)
                ));

        return PageResponse.<TraderListDto>builder()
                .currentPage(traders.getNumber())
                .pageSize(traders.getSize())
                .totalElement(traders.getTotalElements())
                .totalPages(traders.getTotalPages())
                .content(traders.getContent())
                .build();
    }


    /*
        findStrategiesByTrader : 트레이더 별 전략 목록 - 전략 목록 내에서는 똑같이 수익률순 내림차순
    */
    @Override
    public PageResponse<StrategyListByTraderDto> findStrategiesByTrader(Long traderId, Integer pageNum) {
        log.info("traderId in service {} : ", traderId);
        int pageSize = 10;

        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Order.desc("accumProfitRate")));

        // 트레이더 조회
        Member trader = memberRepository.findById(traderId)
                .orElseThrow(() -> new NoSuchElementException("해당 트레이더가 존재하지 않습니다."));

        Page<StrategyListByTraderDto> strategiesByTrader = strategyListRepository.findByTrader(trader, pageable)
                .map(strategy -> new StrategyListByTraderDto(
                        strategy.getTrader().getNickname(),
                        strategy.getId(),
                        strategy.getMethod().getId(),
                        strategy.getCycle(),
                        strategy.getName(),
                        strategy.getMdd(),
                        strategy.getSmScore(),
                        strategy.getAccumProfitRate(),
                        strategy.getFollowerCount()
                ));

        return PageResponse.<StrategyListByTraderDto>builder()
                .currentPage(strategiesByTrader.getNumber())
                .pageSize(strategiesByTrader.getSize())
                .totalElement(strategiesByTrader.getTotalElements())
                .totalPages(strategiesByTrader.getTotalPages())
                .content(strategiesByTrader.getContent())
                .build();
    }
}