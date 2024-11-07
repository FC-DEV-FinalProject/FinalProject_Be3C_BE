package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPostRequestDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface MethodService {
    boolean duplCheck(String name);
    MethodGetResponseDto findById(Long id);
    Page<MethodGetResponseDto> findMethodPage(Integer page);

    boolean insertMethod(MethodPostRequestDto methodPostRequestDto);
}
