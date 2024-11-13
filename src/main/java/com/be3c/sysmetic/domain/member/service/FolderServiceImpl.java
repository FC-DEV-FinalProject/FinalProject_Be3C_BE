package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderPostRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderPutRequestDto;
import com.be3c.sysmetic.domain.member.entity.Folder;
import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.domain.member.repository.FolderRepository;
import com.be3c.sysmetic.domain.member.repository.MemberRepository;
import com.be3c.sysmetic.global.common.Code;
import com.be3c.sysmetic.global.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;

    private final MemberRepository memberRepository;

    private final SecurityUtils securityUtils;

    @Override
    public List<Folder> getUserFolders() {
        Long userId = securityUtils.getUserIdInSecurityContext();

        List<Folder> folderList = folderRepository
                .findAllByMemberIdAndStatusCode(userId, Code.USING_STATE.getCode());

        if(folderList == null || folderList.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return folderList;
    }

    @Override
    public boolean duplCheck(String name) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Optional<Folder> folder = folderRepository
                .findByMemberIdAndFolderNameAndStatusCode(
                        userId,
                        name,
                        Code.USING_STATE.getCode()
                );

        if(folder.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "폴더명이 중복되었습니다.");
        }

        return true;
    }

    @Override
    public boolean insertFolder(FolderPostRequestDto folderPostRequestDto) {
        if(folderPostRequestDto.getCheckDupl()) {
            throw new IllegalArgumentException("중복 확인을 진행해주세요.");
        }

        Long userId = securityUtils.getUserIdInSecurityContext();

        Member member = memberRepository
                .findByIdAndUsingStatusCode(
                        userId,
                        Code.USING_STATE.getCode())
                .orElseThrow(() -> new EntityNotFoundException("폴더 생성 권한이 없습니다."));

        List<Folder> folderList = folderRepository.findAllByMemberIdAndStatusCode(userId, Code.USING_STATE.getCode());

        if (!duplCheck(folderPostRequestDto.getName())) {
            throw new IllegalArgumentException("이미 추가되어 있는 폴더입니다.");
        } else if(folderList.size() >= 5) {
            throw new IllegalArgumentException("폴더는 최대 5개까지 만들 수 있습니다.");
        }

        Folder folder = Folder.builder()
                .folderName(folderPostRequestDto.getName())
                .statusCode(Code.USING_STATE.getCode())
                .member(member)
                .build();

        folderRepository.save(folder);

        return true;
    }

    @Override
    public boolean updateFolder(FolderPutRequestDto folderPutRequestDto) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        if(duplCheck(folderPutRequestDto.getFolderName())) {
            throw new IllegalArgumentException("이미 존재하는 폴더명입니다.");
        }

        Folder folder = folderRepository
                .findByMemberIdAndIdAndStatusCode(
                        userId,
                        folderPutRequestDto.getFolderId(),
                        Code.USING_STATE.getCode()
                ).orElseThrow(() -> new EntityNotFoundException("해당 폴더가 없습니다."));

        if(!folder.getMember().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수정 권한이 없습니다.");
        }

        folder.setFolderName(folderPutRequestDto.getFolderName());
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
                        Code.USING_STATE.getCode()
                ).orElseThrow(() -> new EntityNotFoundException("해당 폴더를 찾을 수 없습니다."));

        find_folder.setStatusCode(Code.NOT_USING_STATE.getCode());

        folderRepository.save(find_folder);

        return false;
    }
}
