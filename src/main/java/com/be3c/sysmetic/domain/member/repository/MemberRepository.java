package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.dto.MemberGetResponseDto;
import com.be3c.sysmetic.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByIdAndUsingStatusCode(Long id, String UsingStatusCode);

    Optional<Member> findByNickname(String nickname);

    @Query(value = "SELECT m.email FROM Member m WHERE m.name = :name AND m.phone_number = :phoneNumber", nativeQuery = true)
    String findEmailByNameAndPhoneNumber(@Param("name") String name, @Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query("UPDATE Member m SET m.password = :newPassword WHERE m.email = :email")
    int updatePasswordByEmail(@Param("email") String email, @Param("newPassword") String newPassword);

    @Modifying
    @Query("UPDATE Member m SET m.roleCode = :roleCode WHERE m.id = :memberId")
    int updateRoleCode(@Param("memberId") Long memberId, @Param("roleCode") String roleCode);

    Page<MemberGetResponseDto> findMembers(
            @Param("role") String role,
            @Param("searchType") String searchType,
            @Param("searchKeyword") String searchKeyword,
            Pageable pageable
    );



}
