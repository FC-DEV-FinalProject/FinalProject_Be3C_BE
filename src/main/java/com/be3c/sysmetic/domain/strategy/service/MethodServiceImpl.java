package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPutRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.exception.ConflictException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MethodServiceImpl implements MethodService {

    private final MethodRepository methodRepository;

    /*
        1. 이름이 같은 활성화된 매매 유형이 있는지 찾는다.
        2. 이름이 같은 활성화된 매매 유형이 존재한다면, return false
        3. 이름이 같은 활성화된 매매 유형이 존재하지 않는다면, return true
     */
    @Override
    public boolean duplCheck(String name) {
        return methodRepository.findByNameAndStatusCode(name, Code.USING_STATE.getCode()).isEmpty();
    }

    @Override
    public MethodGetResponseDto findById(Long id) throws NullPointerException {
        Method method = methodRepository.findByIdAndStatusCode(
                id, Code.USING_STATE.getCode())
                .orElseThrow(EntityNotFoundException::new);
        // 아이콘 파일 패스 찾는 메서드 필요
        return new MethodGetResponseDto(method.getId(), method.getName());
    }

    @Override
    public PageResponse<MethodGetResponseDto> findMethodPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<MethodGetResponseDto> find_page = methodRepository
                .findAllByStatusCode(pageable, Code.USING_STATE.getCode());

        if(!find_page.hasContent()) {
            throw new EntityNotFoundException();
        }

//        파일 패스 찾는 메서드 추가 예정

        return PageResponse.<MethodGetResponseDto>builder()
                .totalElement(find_page.getTotalElements())
                .currentPage(find_page.getNumber())
                .totalPages(find_page.getTotalPages())
                .pageSize(find_page.getNumberOfElements())
                .content(find_page.getContent())
                .build();
    }

    @Override
    public boolean insertMethod(MethodPostRequestDto methodPostRequestDto) {
        if(!methodPostRequestDto.getCheckDuplicate()) {
            throw new IllegalStateException();
        }

        Optional<Method> method = methodRepository.findByNameAndStatusCode(methodPostRequestDto.getName(), Code.USING_STATE.getCode());

        if(method.isPresent()) {
            throw new ConflictException();
        }

        methodRepository.save(Method.builder()
                .name(methodPostRequestDto.getName())
                .statusCode(Code.USING_STATE.getCode())
                .build());

        return true;
    }

    @Override
    public boolean updateMethod(MethodPutRequestDto methodPutRequestDto) {
        if(!methodPutRequestDto.getCheckDuplicate()) {
            throw new IllegalStateException();
        }

        methodRepository.findByNameAndStatusCode(
                methodPutRequestDto.getName(),
                Code.USING_STATE.getCode()
        ).ifPresent(a -> {
            throw new IllegalArgumentException();
        });

        Method method = methodRepository.findByIdAndStatusCode(
                            methodPutRequestDto.getId(),
                            Code.USING_STATE.getCode())
                    .orElseThrow(EntityNotFoundException::new);

        method.setName(methodPutRequestDto.getName());
        methodRepository.save(method);
        /*
            업로드 파일 유무 확인
            업로드 파일 업데이트
         */
        return true;
    }

    @Override
    public boolean deleteMethod(Long id) {
        Method method = methodRepository.findByIdAndStatusCode(
                id, Code.USING_STATE.getCode())
                .orElseThrow(() -> new EntityNotFoundException("해당 매매 유형이 없습니다."));

        method.setStatusCode(Code.NOT_USING_STATE.getCode());
        methodRepository.save(method);
        return true;
    }
}
