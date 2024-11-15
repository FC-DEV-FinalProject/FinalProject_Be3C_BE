package com.be3c.sysmetic.domain.member.service;

import com.be3c.sysmetic.domain.member.dto.FolderListResponseDto;
import com.be3c.sysmetic.domain.member.dto.FolderPostRequestDto;
import com.be3c.sysmetic.domain.member.dto.FolderPutRequestDto;

import java.util.List;

public interface FolderService {
    List<FolderListResponseDto> getUserFolders();
    boolean duplCheck(String name);

    boolean insertFolder(FolderPostRequestDto folderPostRequestDto);
    boolean updateFolder(FolderPutRequestDto folderPutRequestDto);
    boolean deleteFolder(Long folder_id);
}
