package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderGetRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderGetResponseDto;
import com.be3c.sysmetic.domain.member.repository.InterestStrategyRepository;
import com.be3c.sysmetic.global.common.Code;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class InterestStrategyServiceImpl implements InterestStrategyService {

    private final InterestStrategyRepository interestStrategyRepository;

    @Override
    public Page<FolderGetResponseDto> getInterestStrategyPage(
            FolderGetRequestDto folderGetRequestDto,
            Long userId
    ) {
        Pageable pageable = PageRequest.of(
                folderGetRequestDto.getPage(),
                10,
                Sort.by("modifiedAt"));

        Page<FolderGetResponseDto> folderPage = interestStrategyRepository
                .findPageByFolderIdAndStatusCode(
                        folderGetRequestDto.getFolderId(),
                        Code.USING_STATE.getCode(),
                        pageable
                );

        return null;
    }
}
