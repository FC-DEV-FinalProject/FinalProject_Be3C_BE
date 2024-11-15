package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderListResponseDto;
import com.be3c.sysmetic.domain.member.dto.FolderPostRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderPutRequestDto;
import com.be3c.sysmetic.domain.member.entity.Folder;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.exception.ResourceLimitExceededException;
import com.be3c.sysmetic.domain.member.repository.FolderRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.exception.ConflictException;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.be3c.sysmetic.global.common.Code.USING_STATE;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;

    private final MemberRepository memberRepository;

    private final SecurityUtils securityUtils;

    @Override
    public boolean duplCheck(String name) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        return folderRepository
                .findByMemberIdAndNameAndStatusCode(
                        userId,
                        name,
                        USING_STATE.getCode()
                ).isEmpty();
    }

    @Override
    public List<FolderListResponseDto> getUserFolders() {
        Long userId = securityUtils.getUserIdInSecurityContext();

        List<FolderListResponseDto> findFolder = folderRepository.findResponseDtoByMemberIdAndStatusCode(
                userId,
                USING_STATE.getCode()
        );

        if(findFolder == null || findFolder.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return findFolder;
    }

    @Override
    public boolean insertFolder(FolderPostRequestDto folderPostRequestDto) {
        if(folderPostRequestDto.getCheckDupl()) {
            throw new IllegalStateException();
        }

        Long userId = securityUtils.getUserIdInSecurityContext();

        Member member = memberRepository
                .findByIdAndUsingStatusCode(
                        userId,
                        USING_STATE.getCode())
                .orElseThrow(() -> new UsernameNotFoundException(""));

        List<Folder> folderList = folderRepository
                .findAllByMemberIdAndStatusCode(
                        userId,
                        USING_STATE.getCode()
                );

        if (!duplCheck(folderPostRequestDto.getName())) {
            throw new ConflictException();
        } else if(folderList.size() >= 5) {
            throw new ResourceLimitExceededException();
        }

        Folder folder = Folder.builder()
                .name(folderPostRequestDto.getName())
                .statusCode(USING_STATE.getCode())
                .member(member)
                .build();

        folderRepository.save(folder);

        return true;
    }

    @Override
    public boolean updateFolder(FolderPutRequestDto folderPutRequestDto) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        if(!duplCheck(folderPutRequestDto.getFolderName())) {
            throw new ConflictException();
        }

        Folder folder = folderRepository
                .findByMemberIdAndIdAndStatusCode(
                        userId,
                        folderPutRequestDto.getFolderId(),
                        USING_STATE.getCode()
                ).orElseThrow(() -> new UsernameNotFoundException(""));

        if(!folder.getMember().getId().equals(userId)) {
            throw new ConflictException();
        }

        folder.setName(folderPutRequestDto.getFolderName());
        folderRepository.save(folder);

        return true;
    }

    @Override
    public boolean deleteFolder(Long folder_id) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Folder find_folder = folderRepository
                .findByMemberIdAndIdAndStatusCode(
                        userId,
                        folder_id,
                        USING_STATE.getCode()
                ).orElseThrow(EntityNotFoundException::new);

        find_folder.setStatusCode(Code.NOT_USING_STATE.getCode());

        folderRepository.save(find_folder);

        return false;
    }
}
