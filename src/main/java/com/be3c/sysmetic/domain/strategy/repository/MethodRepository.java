package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Method;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MethodRepository extends JpaRepository<Method, Long> {
    Optional<Method> findById(Long id);
    Optional<Method> findByName(String name);
    Optional<Method> findByIdAndStatusCode(Long id, String openStatus);
    Optional<Method> findByNameAndStatusCode(String name, String openStatus);
}
