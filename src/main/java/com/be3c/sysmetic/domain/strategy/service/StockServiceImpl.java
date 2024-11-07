package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StockPutRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.global.common.Code;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockServiceImpl implements StockService {
    private StockRepository stockRepository;

    @Override
    public Stock findItemById(Long id) {
        Optional<Stock> findStock = stockRepository.findByIdAndStatusCode
                (id, Code.valueOf("USING_STATE").getCode());
        if(findStock.isPresent()) {
            return findStock.get();
        }
        throw new EntityNotFoundException("해당 데이터를 찾을 수 없습니다.");
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
        throw new EntityNotFoundException("해당 데이터를 찾을 수 없습니다.");
    }

    @Override
    public Object findItemIcon(Long itemId) {
        // 추가 필요함.
        return Optional.empty();
    }

    @Override
    public boolean saveItem(StockPutRequestDto requestDto) {
        if(stockRepository.findByNameAndStatusCode(
                requestDto.getName(),
                Code.valueOf("USING_STATE").getCode()).isPresent()) {
            throw new IllegalArgumentException("중복된 이름은 저장할 수 없습니다.");
        }

        stockRepository.save(Stock.builder()
                        .name(requestDto.getName())
                        .build());

        // 아이콘 S3에 업로드 + DB에 저장하는 코드 필요
        return true;
    }

    @Override
    public boolean updateItem(StockPutRequestDto requestDto) {
        Stock find_stock = stockRepository.findByIdAndStatusCode(
                requestDto.getId(),
                Code.USING_STATE.getCode())
                .orElseThrow(()-> new EntityNotFoundException("해당 엔티티를 찾을 수 없습니다."));

        find_stock.setName(requestDto.getName());

        // 아이콘 S3에 업로드 + DB 업데이트 코드 필요.

        return true;
    }

    @Override
    public boolean deleteItem(Long id, Long userId) {
        Stock find_stock = stockRepository.findByIdAndStatusCode
                (id, Code.USING_STATE.getCode())
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터를 찾을 수 없습니다."));

        find_stock.setStatusCode(Code.NOT_USING_STATE.getCode());
        find_stock.setModifiedBy(userId);
        stockRepository.save(find_stock);

        return false;
    }
}