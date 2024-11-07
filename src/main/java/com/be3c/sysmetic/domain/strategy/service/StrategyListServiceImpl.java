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

    @Override
    public Page<Strategy> findStrategyPage(Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber,10, Sort.by(Sort.Order.desc("accumProfitRate")));
        String statusCode = "ST001";        // 공개중인 전략
        return strategyListRepository.findAllByStatusCode(statusCode, pageable);
    }
}