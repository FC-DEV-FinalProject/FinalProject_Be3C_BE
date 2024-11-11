package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Folder;
import com.be3c.sysmetic.domain.member.entity.FolderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findByIdAndStatusCode(FolderId folderId, String status);
    List<Folder> findAllByMemberIdAndStatusCode(Long memberId, String statusCode);
    Optional<Folder> findByMemberIdAndFolderNameAndStatusCode(Long memberId, String folderName, String statusCode);

}
