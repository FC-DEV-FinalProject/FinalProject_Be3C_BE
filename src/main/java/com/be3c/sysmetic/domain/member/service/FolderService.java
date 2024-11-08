package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderPostRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderPutRequestDto;
import com.be3c.sysmetic.domain.member.entity.Folder;

import java.util.List;

public interface FolderService {
    List<Folder> getUserFolders(Long id);
    boolean duplCheck(Long id, String name);

    boolean insertFolder(FolderPostRequestDto folderPostRequestDto, Long id);
    boolean updateFolder(FolderPutRequestDto folderPutRequestDto, Long id);
}
