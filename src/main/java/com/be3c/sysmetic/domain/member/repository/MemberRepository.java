package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByIdAndUsingStatusCode(Long id, String UsingStatusCode);

    Optional<Member> findByNickname(String nickname);

    @Query(value = "SELECT m.email FROM Member m WHERE m.name = :name AND m.phone_number = :phoneNumber", nativeQuery = true)
    String findEmailByNameAndPhoneNumber(@Param("name") String name, @Param("phoneNumber") String phoneNumber);
}
