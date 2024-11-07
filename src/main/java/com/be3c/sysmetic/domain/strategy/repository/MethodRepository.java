package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Method;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// TODO 추후 관리자 파트와 머지 후 파일 삭제 예정

@Repository
public interface MethodRepository extends JpaRepository<Method, Long> {
}
