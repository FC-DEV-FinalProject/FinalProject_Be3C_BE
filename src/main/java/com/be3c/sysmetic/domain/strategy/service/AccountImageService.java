package com.be3c.sysmetic.domain.strategy.service;

import com.be3c.sysmetic.domain.strategy.dto.AccountImageDeleteRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.AccountImageRequestDto;
import com.be3c.sysmetic.domain.strategy.dto.AccountImageResponseDto;
import com.be3c.sysmetic.global.common.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AccountImageService {
    PageResponse<AccountImageResponseDto> findAccountImages(Long strategyId, Integer page);
    PageResponse<AccountImageResponseDto> findTraderAccountImages(Long strategyId, Integer page);
    void deleteAccountImage(AccountImageDeleteRequestDto accountImageIdList);
    void saveAccountImage(Long strategyId, List<AccountImageRequestDto> requestDtoList, List<MultipartFile> images);
}