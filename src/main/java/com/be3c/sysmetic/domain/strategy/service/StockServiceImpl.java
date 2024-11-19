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
import com.be3c.sysmetic.global.util.file.dto.FileRequestDto;
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

import static com.be3c.sysmetic.global.common.Code.USING_STATE;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final FileService fileService;

    @Override
    public boolean duplcheck(String name) {
        return stockRepository.findByName(name).isEmpty();
    }

    @Override
    public StockGetResponseDto findItemById(Long id) {
        Stock findStock = stockRepository.findByIdAndStatusCode
                (id, USING_STATE.getCode())
                .orElseThrow(EntityNotFoundException::new);

        String filePath = fileService.getFilePath(new FileRequestDto(FileReferenceType.STOCK, id));

        return StockGetResponseDto.builder()
                .id(findStock.getId())
                .name(findStock.getName())
                .filepath(filePath)
                .build();
    }

    @Override
    public PageResponse<StockGetResponseDto> findItemPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdDate").descending());
        Page<StockGetResponseDto> stockPage = stockRepository.findAllByStatusCode(USING_STATE.getCode(), pageable);

        if (stockPage.hasContent()) {

            for (StockGetResponseDto stockDto : stockPage.getContent()) {
                String filePath = fileService.getFilePath(new FileRequestDto(FileReferenceType.STOCK, stockDto.getId()));
                stockDto.setFilepath(filePath);
            }

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

        Stock savedStock = stockRepository.save(Stock.builder()
                        .name(requestDto.getName())
                        .statusCode(USING_STATE.getCode())
                        .build());

        fileService.uploadImage(requestDto.getStockImage(), new FileRequestDto(FileReferenceType.STOCK, savedStock.getId()));

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

        Stock findStock = stockRepository.findByIdAndStatusCode(
                requestDto.getId(),
                USING_STATE.getCode())
                .orElseThrow(EntityNotFoundException::new);

        findStock.setName(requestDto.getName());

        fileService.updateImage(requestDto.getFile(), new FileRequestDto(FileReferenceType.STOCK, requestDto.getId()));

        return true;
    }

    @Override
    public boolean deleteItem(Long id) {
        Stock findStock = stockRepository.findByIdAndStatusCode
                (
                        id,
                        USING_STATE.getCode()
                ).orElseThrow(EntityNotFoundException::new);

        findStock.setStatusCode(Code.NOT_USING_STATE.getCode());
        stockRepository.save(findStock);

        fileService.deleteFile(new FileRequestDto(FileReferenceType.STOCK, findStock.getId()));

        return true;
    }
}