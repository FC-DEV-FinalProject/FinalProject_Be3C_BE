package com.be3c.sysmetic.domain.member.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdAndUsingStatusCode(Long id, String UsingStatusCode);
}
