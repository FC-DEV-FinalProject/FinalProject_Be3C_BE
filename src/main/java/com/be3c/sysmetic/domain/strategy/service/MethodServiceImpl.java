package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPutRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.PageResponse;
import com.be3c.sysmetic.global.exception.ConflictException;
import com.be3c.sysmetic.global.util.file.dto.FileReferenceType;
import com.be3c.sysmetic.global.util.file.dto.FileRequest;
import com.be3c.sysmetic.global.util.file.service.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.be3c.sysmetic.global.common.Code.NOT_USING_STATE;
import static com.be3c.sysmetic.global.common.Code.USING_STATE;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MethodServiceImpl implements MethodService {

    private final MethodRepository methodRepository;

    private final FileService fileService;

    /*
        1. 이름이 같은 활성화된 매매 유형이 있는지 찾는다.
        2. 이름이 같은 활성화된 매매 유형이 존재한다면, return false
        3. 이름이 같은 활성화된 매매 유형이 존재하지 않는다면, return true
     */
    @Override
    public boolean duplCheck(String name) {
        return methodRepository.findByNameAndStatusCode(name, USING_STATE.getCode()).isEmpty();
    }

    /*
        1. methodId + statusCode를 사용해서 해당 매매 유형을 찾는다.
        1-1. 만약 해당 매매유형이 존재하지 않는다면 EntityNotFoundException을 발생시킨다.
        2. 해당 매매 유형을 MethodGetResponseDto로 변환시킨다.
        2-1. 해당 매매 유형의 아이콘을 찾는다.
        3. MethodGetResponseDto를 반환한다.
     */
    @Override
    public MethodGetResponseDto findById(Long id) throws NullPointerException {
        Method method = methodRepository.findByIdAndStatusCode(
                id,
                USING_STATE.getCode()
        ).orElseThrow(EntityNotFoundException::new);
        return MethodGetResponseDto.builder()
                .id(method.getId())
                .name(method.getName())
                .filePath(fileService.getFilePath(
                        new FileRequest(
                                FileReferenceType.METHOD,
                                method.getId()
                        )))
                .build();
    }

    /*
        1. RequestPage + PageSize + 최신 등록 순서를 사용해 Pageable 객체를 만든다.
        2. pageable + statusCode를 사용해 매매 유형을 찾는다.
        2-1. 만약 해당 페이지에 매매 유형이 한 개도 없을 경우 EntityNotFoundException을 발생시킨다.
        3. 찾은 매매 유형을 하나씩 돌면서 아이콘의 filePath를 찾는 메서드를 실행시킨다.
        4. PageResponse로 변환해 반환한다.
     */
    @Override
    public PageResponse<MethodGetResponseDto> findMethodPage(Integer page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());

        Page<MethodGetResponseDto> methodPage = methodRepository
                .findAllByStatusCode(pageable, USING_STATE.getCode());

        if(!methodPage.hasContent()) {
            throw new EntityNotFoundException();
        }

        methodPage.getContent().forEach(method -> {
            method.setFilePath(fileService.getFilePath(new FileRequest(FileReferenceType.METHOD, method.getId())));
        });

        return PageResponse.<MethodGetResponseDto>builder()
                .totalElement(methodPage.getTotalElements())
                .currentPage(methodPage.getNumber())
                .totalPages(methodPage.getTotalPages())
                .pageSize(methodPage.getNumberOfElements())
                .content(methodPage.getContent())
                .build();
    }

    /*
        1. 만약 중복 확인을 진행하지 않았다면 IllegalStateException을 발생시킨다.
        2. 중복된 이름의 매매 유형이 존재한다면, ConflictException을 발생시킨다.
        2-1. 중복 체크 기능 중 Lock을 거는 기능이 필요한가?
        3. 매매 유형의 아이콘을 저장한다. (미구현)
        4. 해당 매매 유형을 저장한다.
        5. true를 반환해 성공 여부를 알린다.
     */
    @Override
    public boolean insertMethod(MethodPostRequestDto methodPostRequestDto, MultipartFile file) {
        if(!methodPostRequestDto.getCheckDupl()) {
            throw new IllegalStateException();
        }

        if(!duplCheck(methodPostRequestDto.getName())) {
            throw new ConflictException();
        }

        methodRepository.save(Method.builder()
                .name(methodPostRequestDto.getName())
                .statusCode(USING_STATE.getCode())
                .build());

        return true;
    }

    /*
        1. 만약 중복 확인을 진행하지 않았다면 IllegalStateException을 발생시킨다.
        2. 중복된 이름의 매매 유형이 존재한다면, ConflictException을 발생시킨다.
        3. Id + StatusCode를 사용해 해당 매매 유형을 찾는다.
        4. 해당 매매 유형의 이름을 변경한다.
        5. 매매 유형의 아이콘이 변경되었는지 확인한다. (미구현)
        5-1. 매매 유형의 아이콘이 변경되었다면, 파일을 변경한다. (미구현)
        5. true를 반환해 성공 여부를 알린다.
     */
    @Override
    public boolean updateMethod(MethodPutRequestDto methodPutRequestDto, MultipartFile file) {
        if(!methodPutRequestDto.getCheckDuplicate()) {
            throw new IllegalStateException();
        }

        Method method = methodRepository.findByIdAndStatusCode(
                        methodPutRequestDto.getId(),
                        USING_STATE.getCode())
                .orElseThrow(EntityNotFoundException::new);

        if(!(method.getName().equals(methodPutRequestDto.getName()) && duplCheck(methodPutRequestDto.getName()))) {
            throw new ConflictException();
        }

        method.setName(methodPutRequestDto.getName());
        methodRepository.save(method);

        fileService.updateImage(file, new FileRequest(FileReferenceType.METHOD, method.getId()));

        return true;
    }

    /*
        1. methodId + statusCode 해당 매매 유형을 찾는다.
        1-1. 해당 매매 유형이 존재하지 않는다면, EntityNotFoundException을 발생시킨다.
        2. 해당 매매 유형의 상태 코드를 NOT_USING_STATE로 변경한다.
        3. true를 반환해 성공 여부를 알린다.
     */
    @Override
    public boolean deleteMethod(Long id) {
        Method method = methodRepository.findByIdAndStatusCode(
                id,
                USING_STATE.getCode()
        ).orElseThrow(EntityNotFoundException::new);

        method.setStatusCode(NOT_USING_STATE.getCode());
        methodRepository.save(method);

        fileService.deleteFile(new FileRequest(FileReferenceType.METHOD, method.getId()));
        return true;
    }

}
