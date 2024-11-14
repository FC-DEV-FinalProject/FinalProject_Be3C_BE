package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPutRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.exception.ConflictException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.be3c.sysmetic.global.common.Code.USING_STATE;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Override
    public boolean duplcheck(String name) {
        return stockRepository.findByName(name).isEmpty();
    }

    @Override
    public StockGetResponseDto findItemById(Long id) {
        Stock findStock = stockRepository.findByIdAndStatusCode
                (id, USING_STATE.getCode())
                .orElseThrow(EntityNotFoundException::new);
        
        return StockGetResponseDto.builder()
                .id(findStock.getId())
                .name(findStock.getName())
                // filepath 찾는 로직 추가 필요
                .build();
    }

    @Override
    public PageResponse<StockGetResponseDto> findItemPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdDate").descending());
        Page<StockGetResponseDto> stock_page = stockRepository.findAllByStatusCode(USING_STATE.getCode(), pageable);

        if (stock_page.hasContent()) {
            return PageResponse.<StockGetResponseDto>builder()
                    .totalPages(stock_page.getTotalPages())
                    .totalElement(stock_page.getTotalElements())
                    .pageSize(stock_page.getNumberOfElements())
                    .currentPage(page)
                    .build();
        }
        throw new EntityNotFoundException();
    }

    @Override
    public boolean saveItem(StockPostRequestDto requestDto) {
        if(!requestDto.getCheckDuplicate()) {
            throw new IllegalStateException();
        }

        if(stockRepository.findByNameAndStatusCode(
                requestDto.getName(),
                USING_STATE.getCode()
        ).isPresent()) {
            throw new IllegalArgumentException();
        }

        stockRepository.save(Stock.builder()
                        .name(requestDto.getName())
                        .statusCode(USING_STATE.getCode())
                        .build());

        // 아이콘 S3에 업로드 + DB에 저장하는 코드 필요
        return true;
    }

    @Override
    public boolean updateItem(StockPutRequestDto requestDto) {
        if(!requestDto.getCheckDuplicate()) {
            throw new IllegalStateException();
        }

        stockRepository.findByNameAndStatusCode(
                requestDto.getName(),
                USING_STATE.getCode()
        ).ifPresent(a -> {
            throw new ConflictException();
        });

        Stock find_stock = stockRepository.findByIdAndStatusCode(
                requestDto.getId(),
                USING_STATE.getCode())
                .orElseThrow(EntityNotFoundException::new);

        find_stock.setName(requestDto.getName());
        // 아이콘 S3에 업로드 + DB 업데이트 코드 필요.

        return true;
    }

    @Override
    public boolean deleteItem(Long id) {
        Stock find_stock = stockRepository.findByIdAndStatusCode
                (
                        id,
                        USING_STATE.getCode()
                ).orElseThrow(EntityNotFoundException::new);

        find_stock.setStatusCode(Code.NOT_USING_STATE.getCode());
        stockRepository.save(find_stock);

        return true;
    }
}