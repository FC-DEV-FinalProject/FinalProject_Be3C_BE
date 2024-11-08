package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.entity.Folder;
import com.be3c.sysmetic.domain.member.repository.FolderRepository;
import com.be3c.sysmetic.global.common.Code;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FolderServiceImpl implements FolderService {

    FolderRepository folderRepository;

    @Override
    public List<Folder> getUserFolders(Long id) {
        List<Folder> folderList = folderRepository.findByIdAndStatusCode(id, Code.USING_STATE.getCode());

        if(folderList == null || folderList.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return folderList;
    }
}
