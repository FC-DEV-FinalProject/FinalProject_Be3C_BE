package com.be3c.sysmetic.domain.admin.service;

import com.be3c.sysmetic.domain.admin.dto.StockPutRequestDto;
import com.be3c.sysmetic.domain.admin.entity.Stock;
import com.be3c.sysmetic.domain.admin.repository.StockRepository;
import com.be3c.sysmetic.global.common.Code;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockServiceImpl implements StockService {
    private StockRepository stockRepository;

    @Override
    public Stock findItemById(Long id) {
        Optional<Stock> findStock = stockRepository.findByIdAndStatusCode(id, Code.valueOf("USING_STATE").getCode());
        if(findStock.isPresent()) {
            return findStock.get();
        }
        throw new NoSuchElementException();
    }

    @Override
    public boolean duplcheck(String name) {
        return stockRepository.findByName(name).isEmpty();
    }

    @Override
    public Page<Stock> findItemPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdDate").descending());
        Page<Stock> stock_page = stockRepository.findAllByStatusCode(pageable, Code.USING_STATE.getCode());

        if (stock_page.hasContent()) {
            return stock_page;
        }
        throw new NoSuchElementException();
    }

    @Override
    public Object findItemIcon(Long itemId) {
        // 추가 필요함.
        return Optional.empty();
    }

    @Override
    public boolean saveItem(StockPutRequestDto requestDto, Long userId) {
        stockRepository.save(Stock.builder()
                        .name(requestDto.getName())
                        .statusCode(Code.USING_STATE.getCode())
                        .build());

        // 아이콘 S3에 업로드 + DB에 저장하는 코드 필요

        return true;
    }

    @Override
    public boolean updateItem(StockPutRequestDto requestDto, Long userId) {
        Optional<Stock> find_stock = stockRepository.findByIdAndStatusCode(
                requestDto.getId(), Code.USING_STATE.getCode());

        if(find_stock.isEmpty()) {
            return false;
        }

        Stock stock = find_stock.get();
        stock.setName(requestDto.getName());

        // 아이콘 S3에 업로드 + DB 업데이트 코드 필요.

        return true;
    }

    @Override
    public boolean deleteItem(Long id, Long userId) {
        Stock find_stock = stockRepository.findByIdAndStatusCode
                (id, Code.USING_STATE.getCode()).get();

        find_stock.setStatusCode(Code.NOT_USING_STATE.getCode());
        find_stock.setModifiedBy(userId);
        stockRepository.save(find_stock);

        return false;
    }
}