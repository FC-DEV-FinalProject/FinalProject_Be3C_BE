package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Folder;
import com.be3c.sysmetic.domain.member.repository.FolderRepository;
import com.be3c.sysmetic.global.common.Code;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FolderServiceImpl implements FolderService {

    FolderRepository folderRepository;

    @Override
    public List<Folder> getUserFolders(Long id) {
        List<Folder> folderList = folderRepository
                .findByMemberIdAndStatusCode(id, Code.USING_STATE.getCode());

        if(folderList == null || folderList.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return folderList;
    }

    @Override
    public boolean duplCheck(Long id, String name) {
        Optional<Folder> folder = folderRepository
                .findByMemberIdAndFolderNameAndStatusCode(
                        id,
                        name,
                        Code.USING_STATE.getCode()
                );

        if(folder.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "폴더명이 중복되었습니다.");
        }

        return true;
    }
}
