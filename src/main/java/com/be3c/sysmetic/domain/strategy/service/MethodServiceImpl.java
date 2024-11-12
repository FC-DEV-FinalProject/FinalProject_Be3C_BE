package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPutRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.common.response.PageResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
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

    /*

     */
    @Override
    public MethodGetResponseDto findById(Long id) throws NullPointerException {
        Method method = methodRepository.findByIdAndStatusCode(
                id, Code.USING_STATE.getCode())
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터를 찾을 수 없습니다."));
        // 파일 패스 찾는 메서드 필요
        return new MethodGetResponseDto(method.getId(), method.getName());
    }

    /*
        메서드 해당 페이지 데이터 반환
        아이콘 찾기 메서드를 통해 해당 페이지에 넣기.
     */

    @Override
    public PageResponseDto<MethodGetResponseDto> findMethodPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        Page<MethodGetResponseDto> find_page = methodRepository
                .findAllByStatusCode(pageable, Code.USING_STATE.getCode());

        if(!find_page.hasContent()) {
            throw new EntityNotFoundException();
        }

        return PageResponseDto.<MethodGetResponseDto>builder()
                .totalPageCount(find_page.getTotalPages())
                .currentPage(page)
                .itemCountPerPage(find_page.getNumberOfElements())
                .totalItemCount(find_page.getTotalElements())
                .list(find_page.getContent())
                .build();

//        파일 패스 찾는 메서드 추가 예정
//        find_page.getContent().get(0).setFile_path();
    }

    @Override
    public boolean insertMethod(MethodPostRequestDto methodPostRequestDto) {
        if(!methodPostRequestDto.getCheckDuplicate()) {
            throw new IllegalArgumentException("중복 확인을 진행해주세요.");
        }

        Optional<Method> method = methodRepository.findByNameAndStatusCode(methodPostRequestDto.getName(), Code.USING_STATE.getCode());

        if(method.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
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
            throw new IllegalArgumentException("중복 체크를 진행해주세요.");
        }

        Method method = methodRepository.findByIdAndStatusCode(
                methodPutRequestDto.getId(), Code.USING_STATE.getCode())
                .orElseThrow(() -> new EntityNotFoundException("해당 데이터가 없습니다."));

        if(method.getName().equals(methodPutRequestDto.getName())) {
            throw new IllegalArgumentException("이미 적용된 상태입니다.");
        }
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
                .orElseThrow(() -> new EntityNotFoundException("해당 엔티티가 없습니다."));

        if(method.getStatusCode().equals(Code.NOT_USING_STATE.getCode())) {
            throw new IllegalArgumentException("이미 적용된 상태입니다.");
        }

        method.setStatusCode(Code.NOT_USING_STATE.getCode());
        methodRepository.save(method);
        return true;
    }
}
