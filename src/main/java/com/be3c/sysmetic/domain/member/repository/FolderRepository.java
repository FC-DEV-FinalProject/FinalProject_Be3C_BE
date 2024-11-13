package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findByMemberIdAndIdAndStatusCode(Long memberId, Long Id, String statusCode);
    List<Folder> findAllByMemberIdAndStatusCode(Long memberId, String statusCode);
    Optional<Folder> findByMemberIdAndFolderNameAndStatusCode(Long memberId, String folderName, String statusCode);

}
