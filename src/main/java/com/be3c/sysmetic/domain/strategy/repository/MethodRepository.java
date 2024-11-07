package com.be3c.sysmetic.domain.strategy.repository;

import com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto;
import com.be3c.sysmetic.domain.strategy.entity.Method;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MethodRepository extends JpaRepository<Method, Long> {
    Optional<Method> findById(Long id);
    Optional<Method> findByName(String name);

    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto(m.id, m.name) " +
            "FROM Method m WHERE m.id = :id AND m.statusCode = :statusCode")
    Optional<MethodGetResponseDto> findByIdAndStatusCode(Long id, String openStatus);

    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto(m.id, m.name) " +
            "FROM Method m WHERE m.name = :name AND m.statusCode = :statusCode")
    Optional<MethodGetResponseDto> findByNameAndStatusCode(String name, String openStatus);

    // 추후 f.file_path 추가 생각중.
    @Query("SELECT new com.be3c.sysmetic.domain.strategy.dto.MethodGetResponseDto(m.id, m.name) " +
           "FROM Method m WHERE m.statusCode = :statusCode")
    Page<MethodGetResponseDto> findAllByStatusCode(Pageable pageable, String statusCode);
}
