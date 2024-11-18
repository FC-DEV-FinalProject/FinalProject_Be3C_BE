package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPostRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.MethodPutRequestDto;
import com.be3c.sysmetic.global.common.response.PageResponse;

public interface MethodService {
    boolean duplCheck(String name);
    MethodGetResponseDto findById(Long id);
    PageResponse<MethodGetResponseDto> findMethodPage(Integer page);

    boolean insertMethod(MethodPostRequestDto methodPostRequestDto);
    boolean updateMethod(MethodPutRequestDto methodPutRequestDto);
    boolean deleteMethod(Long id);
}