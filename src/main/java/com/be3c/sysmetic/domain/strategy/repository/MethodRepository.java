package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.entity.Method;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MethodRepository extends JpaRepository<Method, Long> {

    Optional<Method> findByName(String name);
}