package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.StockGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.StockPutRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Stock;
import com.be3c.sysmetic.domain.strategy.repository.StockRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.exception.ConflictException;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileService;
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
import org.springframework.web.multipart.MultipartFile;

import static com.be3c.sysmetic.global.common.Code.USING_STATE;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final FileService fileService;

    /*
        종목명 중복 체크 메서드
        1. stockName + statusCode를 사용해 DB 검색
        1-1. 같은 이름의 종목이 존재할 시 return false
        1-2. 같은 이름의 종목이 존재하지 않을 시 return true
     */
    @Override
    public boolean duplCheck(String name) {
        return stockRepository.findByNameAndStatusCode(
                name,
                USING_STATE.getCode()
        ).isEmpty();
    }


    /*
        단일 종목 찾기 메서드
        1. stockId + statusCode를 사용하여 종목 검색
        1-1. 종목이 존재하지 않을 시 EntityNotFoundException을 발생시킨다.
        2. stockGetResponseDto로 변환시켜 반환한다.
     */
    @Override
    public StockGetResponseDto findItemById(Long id) {
        Stock findStock = stockRepository.findByIdAndStatusCode(
                        id,
                        USING_STATE.getCode())
                .orElseThrow(EntityNotFoundException::new);

        String filePath = fileService.getFilePath(new FileRequest(FileReferenceType.STOCK, id));

        return StockGetResponseDto.builder()
                .id(findStock.getId())
                .name(findStock.getName())
                .filePath(filePath)
                .build();
    }

    /*
        종목 찾기 (페이지) 메서드
        1. 현재 페이지 + 페이지 사이즈 + 생성 날짜를 기준으로 Pageable 생성
        2. statusCode + pageable을 사용하여 페이지를 검색
        2-1. 찾은 페이지 객체에 아무 데이터도 존재하지 않는다면 EntityNotFoundException을 발생시킨다.
        3. PageResponse로 변경하여 반환한다.
     */
    @Override
    public PageResponse<StockGetResponseDto> findItemPage(Integer page) {
        // Pageable 반환하는 메서드를 만들어서 사용하는 게 좋지 않을까?
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());
        Page<StockGetResponseDto> stockPage = stockRepository.findAllByStatusCode(USING_STATE.getCode(), pageable);

        if (stockPage.hasContent()) {
            stockPage.getContent().forEach(stock -> {
                stock.setFilePath(fileService.getFilePath(new FileRequest(FileReferenceType.STOCK, stock.getId())));
            });

            return PageResponse.<StockGetResponseDto>builder()
                    .totalPages(stockPage.getTotalPages())
                    .totalElement(stockPage.getTotalElements())
                    .pageSize(stockPage.getNumberOfElements())
                    .currentPage(page)
                    .content(stockPage.getContent())
                    .build();
        }
        throw new EntityNotFoundException();
    }

    /*
        종목 저장 메서드
        1. 중복 체크를 진행하지 않은 요청일 경우 IllegalStateException을 발생시킨다.
        2. 중복된 이름의 종목이 존재할 경우 ConflictException을 발생시킨다.
        3. 종목을 저장한다.
        4. true를 반환해, 성공 여부를 알린다.
     */
    @Override
    public boolean saveItem(StockPostRequestDto stockPostRequestDto, MultipartFile file) {
        if(!stockPostRequestDto.getCheckDuplicate()) {
            throw new IllegalStateException();
        }

        if(!duplCheck(stockPostRequestDto.getName())) {
            throw new ConflictException();
        }

        Stock savedStock = stockRepository.save(Stock.builder()
                        .name(stockPostRequestDto.getName())
                        .statusCode(USING_STATE.getCode())
                        .build());

        fileService.uploadImage(file, new FileRequest(FileReferenceType.STOCK, savedStock.getId()));

        return true;
    }

    /*
        종목 수정 메서드
        1. 중복 체크를 진행하지 않은 요청일 경우 IllegalStateException을 발생시킨다.
        2. 중복된 이름의 종목이 존재할 경우 ConflictException을 발생시킨다.
        3. stockId + statusCode를 통해 수정하려는 종목을 찾는다.
        3-1. 수정할 종목이 존재하지 않을 때, EntityNotFoundException을 발생시킨다.
        4. 종목을 수정한다.
        5. true를 반환해 성공 여부를 알린다.
     */
    @Override
    public boolean updateItem(StockPutRequestDto stockPutRequestDto, MultipartFile file) {
        if(!stockPutRequestDto.getCheckDuplicate()) {
            throw new IllegalStateException();
        }

        if(!duplCheck(stockPutRequestDto.getName())) {
            throw new ConflictException();
        }

        Stock findStock = stockRepository.findByIdAndStatusCode(
                        stockPutRequestDto.getId(),
                        USING_STATE.getCode()
                ).orElseThrow(EntityNotFoundException::new);

        findStock.setName(stockPutRequestDto.getName());

        fileService.updateImage(file, new FileRequest(FileReferenceType.STOCK, findStock.getId()));

        return true;
    }

    /*
        종목 삭제 메서드
        1. stockId + statusCode를 사용해 삭제하려는 종목을 찾는다.
        1-1. 삭제할 종목을 찾지 못했다면, EntityNotFoundException을 발생시킨다.
        2. 종목의 상태를 NOT USING STATE로 변경한다.
        3. 종목을 저장한다.
        4. true를 반환해 성공 여부를 알린다.
     */
    @Override
    public boolean deleteItem(Long id) {
        Stock findStock = stockRepository.findByIdAndStatusCode(
                        id,
                        USING_STATE.getCode()
                ).orElseThrow(EntityNotFoundException::new);

        findStock.setStatusCode(Code.NOT_USING_STATE.getCode());
        stockRepository.save(findStock);

        fileService.deleteFile(new FileRequest(FileReferenceType.STOCK, findStock.getId()));

        return true;
    }
}