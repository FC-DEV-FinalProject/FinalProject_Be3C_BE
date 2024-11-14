package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPutRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.PageResponse;
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

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;

    @Override
    public StockGetResponseDto findItemById(Long id) {
        Stock findStock = stockRepository.findByIdAndStatusCode
                (id, Code.USING_STATE.getCode())
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터를 찾을 수 없습니다."));
        
        return StockGetResponseDto.builder()
                .id(findStock.getId())
                .name(findStock.getName())
                // filepath 찾는 로직 추가 필요
                .build();
    }

    @Override
    public boolean duplcheck(String name) {
        return stockRepository.findByName(name).isEmpty();
    }

    @Override
    public PageResponse<StockGetResponseDto> findItemPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdDate").descending());
        Page<StockGetResponseDto> stock_page = stockRepository.findAllByStatusCode(Code.USING_STATE.getCode(), pageable);

        if (stock_page.hasContent()) {
            return PageResponse.<StockGetResponseDto>builder()
                    .totalPages(stock_page.getTotalPages())
                    .totalElement(stock_page.getTotalElements())
                    .pageSize(stock_page.getNumberOfElements())
                    .currentPage(page)
                    .build();
        }
        throw new EntityNotFoundException("잘못된 페이지 요청입니다.");
    }

    @Override
    public boolean saveItem(StockPostRequestDto requestDto) {
        if(!requestDto.getCheckDuplicate()) {
            throw new IllegalArgumentException("중복 확인을 진행해주세요.");
        }

        if(stockRepository.findByNameAndStatusCode(
                requestDto.getName(),
                Code.valueOf("USING_STATE").getCode()).isPresent()) {
            throw new IllegalArgumentException("중복된 이름은 저장할 수 없습니다.");
        }

        stockRepository.save(Stock.builder()
                        .name(requestDto.getName())
                        .statusCode(Code.USING_STATE.getCode())
                        .build());

        // 아이콘 S3에 업로드 + DB에 저장하는 코드 필요
        return true;
    }

    @Override
    public boolean updateItem(StockPutRequestDto requestDto) {
        if(!requestDto.getCheckDuplicate()) {
            throw new IllegalArgumentException("중복 확인을 진행해주세요.");
        }

        stockRepository.findByNameAndStatusCode(
                requestDto.getName(),
                Code.valueOf("USING_STATE").getCode()
        ).ifPresent(a -> {
            throw new IllegalArgumentException("중복된 이름으로 변경은 불가능합니다.");
        });

        Stock find_stock = stockRepository.findByIdAndStatusCode(
                requestDto.getId(),
                Code.USING_STATE.getCode())
                .orElseThrow(()-> new EntityNotFoundException("해당 엔티티를 찾을 수 없습니다."));

        find_stock.setName(requestDto.getName());
        // 아이콘 S3에 업로드 + DB 업데이트 코드 필요.

        return true;
    }

    @Override
    public boolean deleteItem(Long id) {
        Stock find_stock = stockRepository.findByIdAndStatusCode
                (id, Code.USING_STATE.getCode())
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터를 찾을 수 없습니다."));

        find_stock.setStatusCode(Code.NOT_USING_STATE.getCode());
        stockRepository.save(find_stock);

        return true;
    }
}