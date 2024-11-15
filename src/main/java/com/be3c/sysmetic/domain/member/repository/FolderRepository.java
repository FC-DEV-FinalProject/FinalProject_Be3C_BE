package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.dto.FolderListResponseDto;
import com.be3c.sysmetic.domain.member.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findByMemberIdAndIdAndStatusCode(Long memberId, Long Id, String statusCode);

    List<Folder> findAllByMemberIdAndStatusCode(Long memberId, String statusCode);

    @Query("""
        SELECT new com.be3c.sysmetic.domain.member.dto.FolderListResponseDto(
            f.id, f.name, f.latestInterestStrategyAddedDate
        )
        FROM Folder f
        WHERE f.member.id = :memberId AND f.statusCode = :statusCode
        ORDER BY f.latestInterestStrategyAddedDate DESC
    """)
    List<FolderListResponseDto> findResponseDtoByMemberIdAndStatusCode(
            @Param("memberId") Long memberId,
            @Param("statusCode") String statusCode);

    Optional<Folder> findByMemberIdAndNameAndStatusCode(Long memberId, String folderName, String statusCode);
}
