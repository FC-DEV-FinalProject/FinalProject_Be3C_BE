package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.domain.strategy.repository.StrategyListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class StrategyListServiceImpl implements StrategyListService {

    private final StrategyListRepository strategyListRepository;

    /*
        findStrategyPage : 메인 전략 목록 페이지 (수익률순 조회)
    */
    @Override
    public Page<Strategy> findStrategyPage(Integer pageNum) {
        Pageable pageable = PageRequest.of(pageNum,10, Sort.by(Sort.Order.desc("accumProfitRate")));
        String statusCode = "ST001";        // 공개중인 전략

        return strategyListRepository.findAllByStatusCode(statusCode, pageable);
    }

    /*
        getTotalPageNumber : 특정 statusCode에 따른 전체 페이지 수 계산
    */
    @Override
    public int getTotalPageNumber(String statusCode, int pageSize) {
        long totalStrategyCount = strategyListRepository.countByStatusCode(statusCode);
        return (int) Math.ceil((double) totalStrategyCount / pageSize);
    }
}