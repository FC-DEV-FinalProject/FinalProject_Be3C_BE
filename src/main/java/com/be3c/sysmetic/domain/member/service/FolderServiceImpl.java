package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderListResponseDto;
import com.be3c.sysmetic.domain.member.dto.FolderPostRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderPutRequestDto;
import com.be3c.sysmetic.domain.member.entity.Folder;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.be3c.sysmetic.global.common.Code.USING_STATE;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;

    private final MemberRepository memberRepository;

    private final SecurityUtils securityUtils;

    /*
        1. SecurityContext에서 유저 아이디를 찾는다.
        2. 멤버 아이디 + 사용자가 입력한 폴더 이름 + 상태코드를 사용한 이름 중복 폴더가 존재하는지 확인
        3. 동일한 이름의 폴더 미 존재 시 true 반환 / 존재 시 false 반환
     */
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

    /*
        1. SecurityContext에서 유저 아이디를 찾는다.
        2. userId + 상태코드를 사용해 현재 사용 중인 폴더의 목록을 가져온다.
        2-1. 만약 폴더 목록의 개수가 0개라면, EntityNotFoundException을 발생시킨다.
        3. 찾은 폴더 목록을 반환한다.
     */
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

    /*
        1. 요청에서 중복 확인 인자를 가져온다.
        1-1. 만약 중복 확인을 진행하지 않았다면, IllegalStateException을 발생시킨다.
        2. SecurityContext에서 유저 아이디를 찾는다.
        4. 해당 회원이 가지고 있는 폴더 개수가 5개보다 많다면, ResourceLimitExceededException을 발생시킨다.
        5. 이름을 통해 중복 체크를 진행한다.
        5-1. 중복된 이름이 존재한다면, ConflictException을 발생시킨다.
        6. 폴더를 등록한다.
     */
    @Override
    public boolean insertFolder(FolderPostRequestDto folderPostRequestDto) {
        if(!folderPostRequestDto.getCheckDupl()) {
            throw new IllegalStateException();
        }

        Long userId = securityUtils.getUserIdInSecurityContext();

        if(folderRepository
                .countFoldersByUser(
                        userId,
                        USING_STATE.getCode()
                ) >= 5) {
            throw new ResourceLimitExceededException();
        }

        if (!duplCheck(folderPostRequestDto.getName())) {
            throw new ConflictException();
        }

        folderRepository.save(Folder.builder()
                .name(folderPostRequestDto.getName())
                .statusCode(USING_STATE.getCode())
                .member(memberRepository
                        .findByIdAndUsingStatusCode(
                                userId,
                                USING_STATE.getCode())
                        .orElseThrow(() -> new UsernameNotFoundException("")))
                .build()
        );

        return true;
    }

    /*
        1. 요청에서 중복 확인 여부를 확인한다.
        1-1. 만약 중복 확인을 진행하지 않았다면, IllegalStateException을 발생시킨다.
        2. SecurityContext에서 유저 아이디를 찾는다.
        3. 수정하고자 하는 폴더를 찾는다.
        4. 다른 폴더와 이름이 겹치는지 확인한다.
        4-1. 만약 중복된 이름의 폴더가 존재한다면, ConflictException을 발생시킨다.
        5. 폴더 이름을 수정한다.
     */
    @Override
    public boolean updateFolder(FolderPutRequestDto folderPutRequestDto) {
        if(!folderPutRequestDto.getCheckDupl()) {
            throw new IllegalStateException();
        }

        Long userId = securityUtils.getUserIdInSecurityContext();

        Folder folder = folderRepository
                .findByMemberIdAndIdAndStatusCode(
                        userId,
                        folderPutRequestDto.getFolderId(),
                        USING_STATE.getCode()
                ).orElseThrow(() -> new EntityNotFoundException(""));

        if(!duplCheck(folderPutRequestDto.getFolderName())) {
            throw new ConflictException();
        }

        if(!folder.getMember().getId().equals(userId)) {
            throw new UsernameNotFoundException("");
        }

        folder.setName(folderPutRequestDto.getFolderName());
        folderRepository.save(folder);

        return true;
    }

    /*
        1. 시큐리티 컨텍스트에서 userId를 가져온다.
        2. 해당 유저의 폴더 개수를 얻는다.
        2-1. 폴더 개수가 1개라면, IllegalStateException을 발생시킨다.
        3. 해당 폴더를 찾는다.
        3-1. 폴더를 찾지 못했다면, EntityNotFoundException을 발생시킨다.
        4. 폴더의 사용 상태를 삭제 상태로 바꾼 후 저장한다.
     */
    @Override
    public boolean deleteFolder(Long folder_id) {
        Long userId = securityUtils.getUserIdInSecurityContext();

        Folder find_folder = folderRepository
                .findByMemberIdAndIdAndStatusCode(
                        userId,
                        folder_id,
                        USING_STATE.getCode()
                ).orElseThrow(EntityNotFoundException::new);

        if(folderRepository
                .countFoldersByUser(
                        userId,
                        USING_STATE.getCode()
                ) <= 1) {
            throw new IllegalStateException();
        }

        find_folder.setStatusCode(Code.NOT_USING_STATE.getCode());

        folderRepository.save(find_folder);

        return false;
    }
}
