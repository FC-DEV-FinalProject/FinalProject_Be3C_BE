package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl {

    // 임시
    private final EntityManager em;

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

}
