package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.dto.FolderListResponseDto;
import com.be3c.sysmetic.domain.member.entity.Folder;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
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
            @Param("statusCode") String statusCode
    );

    Optional<Folder> findByMemberIdAndNameAndStatusCode(Long memberId, String folderName, String statusCode);

    Optional<Folder> findByIdAndStatusCode(Long folderId, String statusCode);

    // insert, delete
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT COUNT(f)
        FROM
            Folder f
        WHERE
            f.member.id=:memberId AND
            f.statusCode = :statusCode
    """)
    Long countFoldersByUser(@Param("user") Long memberId, @Param("statusCode") String statusCode);

    // update
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "5000"))
    @Query("""
        SELECT f
        FROM Folder f
        WHERE
            f.member.id = :memberId AND
            f.id = :id AND
            f.statusCode = :statusCode
    """)
    Optional<Folder> findByMemberIdAndIdAndStatusCode(Long memberId, Long id, String statusCode);
}
