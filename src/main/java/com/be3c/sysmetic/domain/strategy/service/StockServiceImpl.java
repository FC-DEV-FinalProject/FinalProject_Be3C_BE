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
    public boolean duplCheck(String name) {
        return stockRepository.findByNameAndStatusCode(
                name,
                USING_STATE.getCode()
        ).isEmpty();
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
        Page<StockGetResponseDto> stockPage = stockRepository.findAllByStatusCode(USING_STATE.getCode(), pageable);

        if (stockPage.hasContent()) {
            return PageResponse.<StockGetResponseDto>builder()
                    .totalPages(stockPage.getTotalPages())
                    .totalElement(stockPage.getTotalElements())
                    .pageSize(stockPage.getNumberOfElements())
                    .currentPage(page)
                    .build();
        }
        throw new EntityNotFoundException();
    }

    @Override
    public boolean saveItem(StockPostRequestDto stockPostRequestDto) {
        if(!stockPostRequestDto.getCheckDuplicate()) {
            throw new IllegalStateException();
        }

        if(!duplCheck(stockPostRequestDto.getName())) {
            throw new ConflictException();
        }

        stockRepository.save(Stock.builder()
                        .name(stockPostRequestDto.getName())
                        .statusCode(USING_STATE.getCode())
                        .build());

        // 아이콘 S3에 업로드 + DB에 저장하는 코드 필요
        return true;
    }

    @Override
    public boolean updateItem(StockPutRequestDto stockPutRequestDto) {
        if(!stockPutRequestDto.getCheckDuplicate()) {
            throw new IllegalStateException();
        }

        if(!duplCheck(stockPutRequestDto.getName())) {
            throw new ConflictException();
        }

        Stock find_stock = stockRepository.findByIdAndStatusCode(
                        stockPutRequestDto.getId(),
                        USING_STATE.getCode()
                ).orElseThrow(EntityNotFoundException::new);

        find_stock.setName(stockPutRequestDto.getName());
        // 아이콘 S3에 업로드 + DB 업데이트 코드 필요.

        return true;
    }

    @Override
    public boolean deleteItem(Long id) {
        Stock findStock = stockRepository.findByIdAndStatusCode(
                        id,
                        USING_STATE.getCode()
                ).orElseThrow(EntityNotFoundException::new);

        findStock.setStatusCode(Code.NOT_USING_STATE.getCode());
        stockRepository.save(findStock);

        return true;
    }
}