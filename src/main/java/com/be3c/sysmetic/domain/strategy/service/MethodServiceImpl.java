package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import com.be3c.sysmetic.domain.strategy.repository.MethodRepository;
import com.be3c.sysmetic.global.common.Code;
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
        return methodRepository.findByIdAndStatusCode(id, Code.USING_STATE.getCode()).get();
    }

    /*
        메서드 해당 페이지 데이터 반환
        아이콘 찾기 메서드를 통해 해당 페이지에 넣기.
     */

    @Override
    public Page<MethodGetResponseDto> findMethodPage(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdDate").descending());
        Page<MethodGetResponseDto> find_page = methodRepository.findAllByStatusCode(pageable, Code.USING_STATE.getCode());
        if(find_page.getContent().isEmpty()) {
            throw new NoSuchElementException();
        }
//        파일 패스 찾는 메서드 추가 예정
//        find_page.getContent().get(0).setFile_path();
        return find_page;
    }

    @Override
    public boolean insertMethod(MethodPostRequestDto methodPostRequestDto) {
        if(methodPostRequestDto.getDuplCheck()) {
            throw new IllegalArgumentException();
        }

        methodRepository.save(Method.builder()
                .name(methodPostRequestDto.getName())
                .statusCode(Code.USING_STATE.getCode())
                .build());

        return true;
    }
}
