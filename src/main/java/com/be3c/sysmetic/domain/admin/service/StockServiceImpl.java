package com.be3c.sysmetic.domain.admin.service;

import com.be3c.sysmetic.domain.admin.dto.StockRequestDto;
import com.be3c.sysmetic.domain.admin.entity.Stock;
import com.be3c.sysmetic.domain.admin.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockServiceImpl implements StockService {
    private StockRepository stockRepository;

    @Override
    public Optional<Stock> findItemById(Long id) {
        return stockRepository.findByIdAndStatusCode(id, "US001");
    }

    @Override
    public Optional<Stock> findItemByName(String name) {
        return stockRepository.findByName(name);
    }

    @Override
    public Page<List<Stock>> findItemPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        return stockRepository.findUsingItemPage(pageable);
    }

    @Override
    public Optional<Object> findItemIcon(Long itemId) {

        return Optional.empty();
    }

    @Override
    public boolean saveItem(StockRequestDto requestDto, Long userId) {
        stockRepository.save(Stock.builder()
                        .name(requestDto.getName())
                        .statusCode("US001")
                        .createdBy(userId)
                        .modifiedBy(userId)
                        .build());
        // 아이콘 S3에 업로드 + DB에 저장하는 코드 필요
        return true;
    }

    @Override
    public boolean updateItem(StockRequestDto requestDto) {
        Optional<Stock> find_stock = stockRepository.findByIdAndStatusCode(
                requestDto.getId(), usingState.valueOf("USING_STATE").getCode());

        if(find_stock.isEmpty()) {
            return false;
        }

        Stock stock = find_stock.get();
        stock.setName(requestDto.getName());

        // 아이콘 업로드 + DB 업데이트 코드 필요.

        return true;
    }
}

enum usingState {
    USING_STATE("US001"),
    NOT_USING_STATE("US002");

    String code;

    usingState(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
